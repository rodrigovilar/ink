package org.ink.eclipse.editors.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.types.ObjectTypeMarker;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.EclipseUtils;
import org.ink.eclipse.utils.SimpleTreeNode;

public class QuickHierarchyOperation extends InkEditorOperation {

	public static final String INK_ECLIPSE_QUICK_HIERARCHY = "ink.eclipse.quickHierarchy";

	@Override
	protected boolean execute(InkObject o, Shell shell, IDocument doc, IFile file) throws Exception {
		if (o != null) {
			SimpleTreeNode<Mirror> originalObjectNode = createOriginalObjectNode(o);
			SimpleTreeNode<Mirror> hierarchyTreeRoot = createHierarchyTree(o, originalObjectNode);
			displayTree(hierarchyTreeRoot, originalObjectNode, shell);
		}
		return false;
	}

	private SimpleTreeNode<Mirror> createOriginalObjectNode(InkObject inkObject) {
		return new SimpleTreeNode<Mirror>(inkObject.<Mirror> reflect());
	}

	private SimpleTreeNode<Mirror> createHierarchyTree(InkObject inkObject, SimpleTreeNode<Mirror> originalObjectNode) {
		SimpleTreeNode<Mirror> rootNode = originalObjectNode;
		Mirror superMirror = originalObjectNode.getValue().getSuper();
		while (superMirror != null) {
			SimpleTreeNode<Mirror> currentNode = new SimpleTreeNode<Mirror>(superMirror);
			currentNode.addChildNode(rootNode);
			rootNode = currentNode;
			superMirror = superMirror.getSuper();
		}

		ModelInfoRepository modelInfoRepository = ModelInfoFactory.getInstance();
		addDescendents(inkObject, originalObjectNode, modelInfoRepository);
		return rootNode;
	}

	protected void addDescendents(InkObject inkObject, SimpleTreeNode<Mirror> objectNode, ModelInfoRepository modelInfoRepository) {
		Collection<InkObject> descendents = modelInfoRepository.findReferrers(inkObject, ExtendsRelation.getInstance(), false);
		for (InkObject descendent : descendents) {
			SimpleTreeNode<Mirror> descendentNode = new SimpleTreeNode<Mirror>(descendent.reflect());
			objectNode.addChildNode(descendentNode);
			addDescendents(descendent, descendentNode, modelInfoRepository);
		}
	}

	private void displayTree(SimpleTreeNode<Mirror> hierarchyTreeRoot, SimpleTreeNode<Mirror> originalObjectNode, Shell shell) {
		new QuickHierarchyDialog(hierarchyTreeRoot, originalObjectNode, new JumpHandler<Mirror>() {

			@Override
			public void doJump(Mirror object) {
				EclipseUtils.openEditor(object);
			}
		}, "Type hierarchy for '" + originalObjectNode.getValue().getId() + "'", shell).open();
	}

	private static interface JumpHandler<T> {
		public void doJump(T object);
	}

	private static class QuickHierarchyDialog<T> extends PopupDialog {

		private final SimpleTreeNode<T> rootNode;
		private final SimpleTreeNode<T> originalObjectNode;
		private final JumpHandler<T> jumpHandler;
		private final TriggerSequence keyBinding;

		public QuickHierarchyDialog(SimpleTreeNode<T> rootNode, SimpleTreeNode<T> originalObjectNode, JumpHandler<T> jumpHandler, String title, Shell shell) {
			super(shell, SWT.RESIZE, true, true, false, true, true, null, null);
			this.rootNode = rootNode;
			this.originalObjectNode = originalObjectNode;
			this.jumpHandler = jumpHandler;
			TriggerSequence[] activeBindings = ((IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class)).getActiveBindingsFor(INK_ECLIPSE_QUICK_HIERARCHY);
			if (activeBindings != null && activeBindings.length > 0) {
				this.keyBinding = activeBindings[0];
				setInfoText("Press '" + keyBinding.format() + "' to see the instance-of hierarchy");
			} else {
				this.keyBinding = null;
			}
			setTitleText(title);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			TreeViewer fTreeViewer = createTreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
			final Tree tree = fTreeViewer.getTree();
			tree.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
					KeySequence keySequence = KeySequence.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
					if (keyBinding != null && keyBinding.equals(keySequence)) {
						e.doit = false;
						//toggleHierarchy();
					} else if (e.character == 0x1B) {
						close();
					} else if (e.character == 0xD) {
						callJumpHandler(tree);
					}
				}
			});
			tree.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					callJumpHandler(tree);
				}
			});
			return fTreeViewer.getControl();
		}

		private void callJumpHandler(Tree tree) {
			TreeItem[] selection = tree.getSelection();
			if (selection != null && selection.length > 0 && jumpHandler != null) {
				TreeNode selectedNode = (TreeNode) selection[0].getData();
				jumpHandler.doJump((T) selectedNode.getValue());
			}
			close();
		}

		protected TreeViewer createTreeViewer(Composite parent, int style) {
			Tree tree = new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = tree.getItemHeight() * 12;
			tree.setLayoutData(gd);

			TreeViewer treeViewer = new TreeViewer(tree);
			treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
			treeViewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new InkLabelProvider(), null, null));
			treeViewer.setContentProvider(new TreeNodeContentProvider());
			TreeNode originalObjectEclipseNode = new TreeNode(originalObjectNode.getValue());
			treeViewer.setInput(new TreeNode[] { toEclipseTreeNode(rootNode, originalObjectEclipseNode) });
			TreeNode tempNodeForSelection = originalObjectEclipseNode;
			List<TreeNode> pathToOriginalObjectNode = new ArrayList<TreeNode>();
			while (tempNodeForSelection != null) {
				pathToOriginalObjectNode.add(0, tempNodeForSelection);
				tempNodeForSelection = tempNodeForSelection.getParent();
			}
			treeViewer.setSelection(new TreeSelection(new TreePath(pathToOriginalObjectNode.toArray())));
			return treeViewer;
		}

		private TreeNode toEclipseTreeNode(SimpleTreeNode<T> node, TreeNode originalObjectEclipseNode) {
			TreeNode result = node == originalObjectNode ? originalObjectEclipseNode : new TreeNode(node.getValue());
			List<SimpleTreeNode<T>> children = node.getChildren();
			TreeNode[] eclipseChildren = new TreeNode[children.size()];
			int currentChild = 0;
			for (SimpleTreeNode<T> child : children) {
				eclipseChildren[currentChild] = toEclipseTreeNode(child, originalObjectEclipseNode);
				eclipseChildren[currentChild].setParent(result);
				currentChild++;
			}
			result.setChildren(eclipseChildren);
			return result;
		}
	}

	private static class InkLabelProvider extends LabelProvider implements IStyledLabelProvider {

		private static final String STRUCT = "Struct";
		private final Map<String, Image> imagesMap;
		private final static Map<String, String> filenamesMap;
		static {
			filenamesMap = new HashMap<String, String>(5);
			filenamesMap.put(ObjectTypeMarker.Metaclass.toString(), "metaclass.png");
			filenamesMap.put(ObjectTypeMarker.Class.toString(), "class.png");
			filenamesMap.put(ObjectTypeMarker.Object.toString(), "object.png");
			filenamesMap.put(ObjectTypeMarker.Enumeration.toString(), "enum.png");
			filenamesMap.put(STRUCT, "struct.png");
		}

		public InkLabelProvider() {
			imagesMap = new HashMap<String, Image>(4);
		}

		@Override
		public StyledString getStyledText(Object element) {
			Mirror mirror = (Mirror) ((TreeNode) element).getValue();
			StyledString result = new StyledString(mirror.getShortId());
			result.append(" - " + mirror.getNamespace(), StyledString.QUALIFIER_STYLER);
			return result;
		}

		@Override
		public Image getImage(Object element) {
			Mirror mirror = (Mirror) ((TreeNode) element).getValue();
			ObjectTypeMarker objectType = mirror.getObjectTypeMarker();
			String imageKey = (objectType == ObjectTypeMarker.Class && ((ClassMirror) mirror).isStruct()) ? STRUCT : objectType.toString();
			Image result = imagesMap.get(imageKey);
			if (result == null) {
				result = ImageDescriptor.createFromFile(InkPlugin.class, "/resources/icons/" + filenamesMap.get(imageKey)).createImage(true);
				imagesMap.put(imageKey, result);
			}
			return result;
		}

		@Override
		public void dispose() {
			for (Image image : imagesMap.values()) {
				image.dispose();
			}
			super.dispose();
		}
	}
}