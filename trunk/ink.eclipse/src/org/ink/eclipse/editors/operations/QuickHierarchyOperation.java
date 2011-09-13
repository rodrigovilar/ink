package org.ink.eclipse.editors.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.types.ObjectTypeMarker;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.SimpleTreeNode;

public class QuickHierarchyOperation extends InkEditorOperation {

	@Override
	protected boolean execute(InkObject o, Shell shell, IDocument doc, IFile file) throws Exception {
		if (o != null) {
			SimpleTreeNode<Mirror> hierarchyTree = createHierarchyTree(o);
			displayTree(hierarchyTree, shell);
		}
		return false;
	}

	private SimpleTreeNode<Mirror> createHierarchyTree(InkObject inkObject) {
		Mirror mirror = inkObject.reflect();
		SimpleTreeNode<Mirror> originalObjectNode = new SimpleTreeNode<Mirror>(mirror);

		SimpleTreeNode<Mirror> rootNode = originalObjectNode;
		Mirror superMirror = mirror.getSuper();
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

	private void displayTree(SimpleTreeNode<?> hierarchyTree, Shell shell) {
		new QuickHierarchyDialog(hierarchyTree, shell).open();
	}

	private static class QuickHierarchyDialog extends PopupDialog {

		private final SimpleTreeNode<?> node;

		public QuickHierarchyDialog(SimpleTreeNode<?> node, Shell shell) {
			super(shell, SWT.RESIZE, true, true, false, true, true, null, null);
			this.node = node;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			TreeViewer fTreeViewer = createTreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL);
			final Tree tree = fTreeViewer.getTree();
			tree.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.character == 0x1B) {
						close();
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// do nothing
				}
			});

			return fTreeViewer.getControl();
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
			treeViewer.setInput(new TreeNode[] { toEclipseTreeNode(node) });
			return treeViewer;
		}

		private <T> TreeNode toEclipseTreeNode(SimpleTreeNode<T> node) {
			TreeNode result = new TreeNode(node.getValue());
			List<SimpleTreeNode<T>> children = node.getChildren();
			TreeNode[] eclipseChildren = new TreeNode[children.size()];
			int currentChild = 0;
			for (SimpleTreeNode<T> child : children) {
				eclipseChildren[currentChild] = toEclipseTreeNode(child);
				eclipseChildren[currentChild].setParent(result);
				currentChild++;
			}
			result.setChildren(eclipseChildren);
			return result;
		}
	}

	private static class InkLabelProvider extends LabelProvider implements IStyledLabelProvider {

		private final Map<ObjectTypeMarker, Image> imagesMap;

		public InkLabelProvider() {
			imagesMap = new HashMap<ObjectTypeMarker, Image>(4);
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
			Image result = imagesMap.get(objectType);
			if (result == null) {
				String filename = null;
				switch (objectType) {
				case Metaclass:
					filename = "metaclass.png";
					break;
				case Class:
					filename = "class.png";
					break;
				case Object:
					filename = "object.png";
					break;
				case Enumeration:
					filename = "sample.gif";
					break;
				}
				result = ImageDescriptor.createFromFile(InkPlugin.class, "/resources/icons/" + filename).createImage(true);
				imagesMap.put(objectType, result);
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