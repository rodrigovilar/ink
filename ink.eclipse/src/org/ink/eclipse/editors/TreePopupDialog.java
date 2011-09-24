package org.ink.eclipse.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.SimpleTreeNode;

public class TreePopupDialog<T> extends PopupDialog {

	public static interface JumpHandler<T> {
		public void doJump(T object);
	}

	protected SimpleTreeNode<T> rootNode;
	protected SimpleTreeNode<T> selectedNode;
	protected final JumpHandler<T> jumpHandler;
	protected KeyAdapter keyAdapter;
	protected IStyledLabelProvider labelProvider;
	protected Tree tree;
	protected TreeViewer treeViewer;

	public TreePopupDialog(SimpleTreeNode<T> rootNode, SimpleTreeNode<T> selectedNode, JumpHandler<T> jumpHandler, String title, Shell shell) {
		super(shell, SWT.RESIZE, true, true, false, true, true, null, null);
		this.rootNode = rootNode;
		this.selectedNode = selectedNode;
		this.jumpHandler = jumpHandler;
		this.keyAdapter = new TreePopupDialogKeyAdapter();
		this.labelProvider = new TreePopupDialogLabelProvider<T>();
		setTitleText(title);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		tree = new Tree(parent, SWT.SINGLE | ((SWT.V_SCROLL | SWT.H_SCROLL) & ~SWT.MULTI));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = tree.getItemHeight() * 12;
		tree.setLayoutData(gd);

		treeViewer = new TreeViewer(tree);
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		treeViewer.setLabelProvider(new DecoratingStyledCellLabelProvider(labelProvider, null, null));
		treeViewer.setContentProvider(new TreeNodeContentProvider());
		doSetInput();

		tree.addKeyListener(keyAdapter);
		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				callJumpHandler();
			}
		});
		return treeViewer.getControl();
	}

	@SuppressWarnings("unchecked")
	private void callJumpHandler() {
		TreeItem[] selection = tree.getSelection();
		if (selection != null && selection.length > 0 && jumpHandler != null) {
			TreeNode selectedNode = (TreeNode) selection[0].getData();
			jumpHandler.doJump((T) selectedNode.getValue());
		}
		close();
	}

	protected void doSetInput() {
		TreeNode selectedEclipseNode = new TreeNode(selectedNode.getValue());
		treeViewer.setInput(new TreeNode[] { toEclipseTreeNode(rootNode, selectedEclipseNode) });
		TreeNode tempNodeForSelection = selectedEclipseNode;
		List<TreeNode> pathToOriginalObjectNode = new ArrayList<TreeNode>();
		while (tempNodeForSelection != null) {
			pathToOriginalObjectNode.add(0, tempNodeForSelection);
			tempNodeForSelection = tempNodeForSelection.getParent();
		}
		treeViewer.setSelection(new TreeSelection(new TreePath(pathToOriginalObjectNode.toArray())));
	}

	private TreeNode toEclipseTreeNode(SimpleTreeNode<T> node, TreeNode selectedEclipseNode) {
		TreeNode result = node == selectedNode ? selectedEclipseNode : new TreeNode(node.getValue());
		List<SimpleTreeNode<T>> children = node.getChildren();
		TreeNode[] eclipseChildren = new TreeNode[children.size()];
		int currentChild = 0;
		for (SimpleTreeNode<T> child : children) {
			eclipseChildren[currentChild] = toEclipseTreeNode(child, selectedEclipseNode);
			eclipseChildren[currentChild].setParent(result);
			currentChild++;
		}
		result.setChildren(eclipseChildren);
		return result;
	}

	protected static class TreePopupDialogLabelProvider<T> extends LabelProvider implements IStyledLabelProvider {

		private final Map<String, Image> imagesMap;

		public TreePopupDialogLabelProvider() {
			imagesMap = new HashMap<String, Image>(4);
		}

		@SuppressWarnings("unchecked")
		@Override
		public final StyledString getStyledText(Object element) {
			return doGetStyledText((T) ((TreeNode) element).getValue());
		}

		@Override
		public final Image getImage(Object element) {
			Image result = null;
			@SuppressWarnings("unchecked")
			String filename = doGetImageFilename((T) ((TreeNode) element).getValue());
			if (filename != null) {
				result = imagesMap.get(filename);
				if (result == null) {
					result = ImageDescriptor.createFromFile(InkPlugin.class, "/resources/icons/" + filename).createImage(true);
					imagesMap.put(filename, result);
				}
			}
			return result;
		}

		protected StyledString doGetStyledText(T element) {
			return new StyledString(element.toString());
		}

		protected String doGetImageFilename(T element) {
			return null;
		}

		@Override
		public void dispose() {
			for (Image image : imagesMap.values()) {
				image.dispose();
			}
			super.dispose();
		}
	}

	protected class TreePopupDialogKeyAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.character == java.awt.event.KeyEvent.VK_ESCAPE) {
				close();
			} else if (e.character == 0xD) {
				callJumpHandler();
			}
		}
	}
}
