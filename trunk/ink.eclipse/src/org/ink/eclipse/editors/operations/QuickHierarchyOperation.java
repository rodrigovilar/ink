package org.ink.eclipse.editors.operations;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;
import org.ink.eclipse.utils.SimpleTreeNode;

public class QuickHierarchyOperation extends InkEditorOperation {

	@Override
	protected boolean execute(InkObject o, Shell shell, IDocument doc, IFile file) throws Exception {
		if (o != null) {
			SimpleTreeNode<String> hierarchyTree = createHierarchyTree(o);
			displayTree(hierarchyTree, shell);
		}
		return false;
	}

	private SimpleTreeNode<String> createHierarchyTree(InkObject inkObject) {
		Mirror mirror = inkObject.reflect();
		Mirror superMirror = mirror.getSuper();
		ClassMirror classMirror = mirror.getClassMirror();

		SimpleTreeNode<String> result = new SimpleTreeNode<String>(classMirror.getId());
		SimpleTreeNode<String> originalObjectNode = new SimpleTreeNode<String>(mirror.getId());
		if (superMirror == null || superMirror.getId().equals(classMirror.getId())) {
			result.addChildNode(originalObjectNode);
		} else {
			SimpleTreeNode<String> superMirrorNode = new SimpleTreeNode<String>(superMirror.getId());
			result.addChildNode(superMirrorNode);
			superMirrorNode.addChildNode(originalObjectNode);
		}

		ModelInfoRepository modelInfoRepository = ModelInfoFactory.getInstance();
		Collection<InkObject> descendents = modelInfoRepository.findReferrers(inkObject, ExtendsRelation.getInstance(), false);
		for (InkObject descendent : descendents) {
			originalObjectNode.addChild(descendent.reflect().getId());
		}

		Collection<InkObject> instances = modelInfoRepository.findReferrers(inkObject, IsInstanceOfRelation.getInstance(), false);
		for (InkObject instance : instances) {
			originalObjectNode.addChild(instance.reflect().getId());
		}

		return result;
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
			treeViewer.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					return ((TreeNode) element).getValue().toString();
				}
			});
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
}