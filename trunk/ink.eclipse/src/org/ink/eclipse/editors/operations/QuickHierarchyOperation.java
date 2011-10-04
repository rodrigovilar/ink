package org.ink.eclipse.editors.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;
import org.ink.core.vm.modelinfo.relations.ModelRelation;
import org.ink.core.vm.types.ObjectTypeMarker;
import org.ink.eclipse.editors.TreePopupDialog;
import org.ink.eclipse.editors.TreePopupDialog.JumpHandler;
import org.ink.eclipse.utils.EclipseUtils;
import org.ink.eclipse.utils.SimpleTreeNode;

public class QuickHierarchyOperation extends InkEditorOperation {

	public static final String INK_ECLIPSE_QUICK_HIERARCHY = "ink.eclipse.quickHierarchy";

	private InkObject originalObject;

	@Override
	protected boolean execute(InkObject o, Shell shell, IDocument doc, IFile file) throws Exception {
		if (o != null) {
			this.originalObject = o;
			displayTree(shell);
		}
		return false;
	}

	private SimpleTreeNode<Mirror>[] doBuildHierarchy(HierarchyType type) {
		@SuppressWarnings("unchecked")
		SimpleTreeNode<Mirror>[] result = new SimpleTreeNode[2];
		SimpleTreeNode<Mirror> originalObjectNode = new SimpleTreeNode<Mirror>(originalObject.<Mirror> reflect());
		SimpleTreeNode<Mirror> rootNode = originalObjectNode;
		Mirror nextMirror = type.getNextMirror(originalObjectNode.getValue());
		while (nextMirror != null) {
			SimpleTreeNode<Mirror> currentNode = new SimpleTreeNode<Mirror>(nextMirror);
			currentNode.addChildNode(rootNode);
			rootNode = currentNode;
			Mirror nextMirrorCandidate = type.getNextMirror(nextMirror);
			nextMirror = nextMirror == nextMirrorCandidate ? null : nextMirrorCandidate;
		}

		ModelInfoRepository modelInfoRepository = ModelInfoFactory.getInstance();
		addDescendents(originalObject, originalObjectNode, modelInfoRepository, type.getRelation());
		result[0] = rootNode;
		result[1] = originalObjectNode;
		return result;
	}

	protected void addDescendents(InkObject inkObject, SimpleTreeNode<Mirror> objectNode, ModelInfoRepository modelInfoRepository, ModelRelation relation) {
		Collection<Mirror> descendents = modelInfoRepository.findReferrers(inkObject.reflect(), relation, false);
		for (Mirror descendent : descendents) {
			SimpleTreeNode<Mirror> descendentNode = new SimpleTreeNode<Mirror>(descendent);
			objectNode.addChildNode(descendentNode);
			addDescendents(descendent, descendentNode, modelInfoRepository, relation);
		}
	}

	private void displayTree(Shell shell) {
		new QuickHierarchyDialog(new HierarchyBuilder() {

			@Override
			public SimpleTreeNode<Mirror>[] buildHierarchy(HierarchyType type) {
				return doBuildHierarchy(type);
			}
		}, new JumpHandler<Mirror>() {

			@Override
			public void doJump(Mirror object) {
				EclipseUtils.openEditor(object);
			}
		}, shell).open();
	}

	private static interface HierarchyBuilder {
		SimpleTreeNode<Mirror>[] buildHierarchy(HierarchyType type);
	}

	private static enum HierarchyType {

		EXTENDS {

			@Override
			public Mirror getNextMirror(Mirror value) {
				return value.getSuper();
			}

			@Override
			public ModelRelation getRelation() {
				return ExtendsRelation.getInstance();
			}

			@Override
			public String getLabel() {
				return "Type";
			}
		},
		INSTANCE_OF {
			@Override
			public Mirror getNextMirror(Mirror value) {
				return value.getClassMirror();
			}

			@Override
			public ModelRelation getRelation() {
				return IsInstanceOfRelation.getInstance();
			}

			@Override
			public String getLabel() {
				return "Instance-of";
			}
		};

		protected abstract Mirror getNextMirror(Mirror value);

		protected abstract ModelRelation getRelation();

		protected abstract String getLabel();

		protected HierarchyType getNext() {
			HierarchyType[] allValues = values();
			return allValues[(ordinal() + 1) % allValues.length];
		}
	}

	private static class QuickHierarchyDialog extends TreePopupDialog<Mirror> {

		protected final TriggerSequence keyBinding;

		private HierarchyType currentHierarchyType;
		private final HierarchyBuilder hierarchyBuilder;

		public QuickHierarchyDialog(HierarchyBuilder hierarchyBuilder, JumpHandler<Mirror> jumpHandler, Shell shell) {
			super(null, null, jumpHandler, null, shell);
			this.hierarchyBuilder = hierarchyBuilder;
			TriggerSequence[] activeBindings = ((IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class)).getActiveBindingsFor(INK_ECLIPSE_QUICK_HIERARCHY);
			this.keyBinding = activeBindings != null && activeBindings.length > 0 ? activeBindings[0] : null;
			this.keyAdapter = new QuickHierarchyKeyAdapter();
			this.labelProvider = new QuickHierarchyLabelProvider();
			applyHierarchy(HierarchyType.EXTENDS);
		}

		private void applyHierarchy(HierarchyType type) {
			currentHierarchyType = type;
			SimpleTreeNode<Mirror>[] nodes = hierarchyBuilder.buildHierarchy(type);
			this.rootNode = nodes[0];
			this.selectedNode = nodes[1];
			setTitleText(type.getLabel() + " hierarchy for '" + selectedNode.getValue().getId() + "'");
			if (keyBinding != null) {
				setInfoText("Press '" + keyBinding.format() + "' to see the " + type.getNext().getLabel().toLowerCase() + " hierarchy");
			}
		}

		private void toggleHierarchy() {
			applyHierarchy(currentHierarchyType.getNext());
			doSetInput();
		}

		protected class QuickHierarchyKeyAdapter extends TreePopupDialogKeyAdapter {

			@Override
			public void keyPressed(KeyEvent e) {
				int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
				KeySequence keySequence = KeySequence.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
				if (keyBinding != null && keyBinding.equals(keySequence)) {
					e.doit = false;
					toggleHierarchy();
				} else {
					super.keyPressed(e);
				}
			}
		}

		private static class QuickHierarchyLabelProvider extends TreePopupDialogLabelProvider<Mirror> {

			private static final String STRUCT = "Struct";
			private final static Map<String, String> filenamesMap;
			static {
				filenamesMap = new HashMap<String, String>(5);
				filenamesMap.put(ObjectTypeMarker.Metaclass.toString(), "metaclass.png");
				filenamesMap.put(ObjectTypeMarker.Class.toString(), "class.png");
				filenamesMap.put(ObjectTypeMarker.Object.toString(), "object.png");
				filenamesMap.put(ObjectTypeMarker.Enumeration.toString(), "enum.png");
				filenamesMap.put(STRUCT, "struct.png");
			}

			@Override
			protected StyledString doGetStyledText(Mirror element) {
				StyledString result = new StyledString(element.getShortId());
				result.append(" - " + element.getNamespace(), StyledString.QUALIFIER_STYLER);
				return result;
			}

			@Override
			protected String doGetImageFilename(Mirror element) {
				ObjectTypeMarker objectType = element.getObjectTypeMarker();
				String imageKey = (objectType == ObjectTypeMarker.Class && ((ClassMirror) element).isStruct()) ? STRUCT : objectType.toString();
				return filenamesMap.get(imageKey);
			}
		}
	}
}