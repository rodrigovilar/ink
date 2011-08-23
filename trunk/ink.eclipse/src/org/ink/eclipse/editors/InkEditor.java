package org.ink.eclipse.editors;



import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.ink.eclipse.editors.document.InkDocumentProvider;
import org.ink.eclipse.editors.operations.GenerateJavaOperation;
import org.ink.eclipse.editors.operations.GotoInkOperation;
import org.ink.eclipse.editors.operations.GotoJavaOperation;
import org.ink.eclipse.editors.operations.InkEditorOperation;
import org.ink.eclipse.editors.utils.ColorManager;

public class InkEditor extends TextEditor{

	private static final String INK_ECLIPSE_GOTO_ELEMENT = "ink.eclipse.gotoElement";

	private static final String INK_ECLIPSE_GOTO_JAVA = "ink.eclipse.gotoJava";

	private static final String INK_ECLIPSE_GENERATE_JAVA = "ink.eclipse.generateJava";

	public static final String EDITOR_ID = "ink.eclipse.editors.InkEditor";

	private final ColorManager colorManager;

	public InkEditor() {
		setEditorContextMenuId("#InkEditorContext");
		//setRulerContextMenuId("#InkEditorContext");
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new InkSourceViewerConfiguration(colorManager));
		setDocumentProvider(new InkDocumentProvider());
	}

	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "ink.eclipse.context.editor" });  //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(Composite, IVerticalRuler, int)
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {

		fAnnotationAccess= getAnnotationAccess();
		fOverviewRuler= createOverviewRuler(getSharedColors());

		ISourceViewer viewer= new InkSourceViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);


		return viewer;
	}

	@Override
	protected void createActions() {
		super.createActions();

		ResourceBundle bundle = ResourceBundle.getBundle(InkMessages.class.getName());
		TextOperationAction action= new TextOperationAction(bundle,"generate_ java", this, 300, true);
		action.setText("Generate Java Classes");
		action.setToolTipText("Generate Java Classes");
		action.setActionDefinitionId(INK_ECLIPSE_GENERATE_JAVA);
		action.setEnabled(true);
		setAction(INK_ECLIPSE_GENERATE_JAVA, action);

		action= new TextOperationAction(bundle,"goto_java", this, 200, true);
		action.setText("Goto Java");
		action.setToolTipText("Goto Java");
		action.setActionDefinitionId(INK_ECLIPSE_GOTO_JAVA);
		action.setEnabled(true);
		setAction(INK_ECLIPSE_GOTO_JAVA, action);

		action= new TextOperationAction(bundle,"goto_ink", this, 100, true);
		action.setText("Goto Ink Element");
		action.setToolTipText("Goto Ink Element");
		action.setActionDefinitionId(INK_ECLIPSE_GOTO_ELEMENT);
		action.setEnabled(true);
		setAction(INK_ECLIPSE_GOTO_ELEMENT, action);
		action= new TextOperationAction(bundle,"goto_ink", this, 100, true);

	}

	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		menu.insertAfter(IContextMenuConstants.GROUP_OPEN, new GroupMarker(IContextMenuConstants.GROUP_SHOW));
		IAction action= getAction(INK_ECLIPSE_GENERATE_JAVA);
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);

		menu.insertAfter(IContextMenuConstants.GROUP_OPEN, new GroupMarker(IContextMenuConstants.GROUP_SHOW));
		action= getAction(INK_ECLIPSE_GOTO_JAVA);
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);

		menu.insertAfter(IContextMenuConstants.GROUP_OPEN, new GroupMarker(IContextMenuConstants.GROUP_SHOW));
		action= getAction(INK_ECLIPSE_GOTO_ELEMENT);
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);

	}

	public class InkSourceViewer extends ProjectionViewer{

		Map<Integer, InkEditorOperation> codeToOps = new HashMap<Integer, InkEditorOperation>();


		public InkSourceViewer(Composite parent, IVerticalRuler ruler,
				IOverviewRuler overviewRuler, boolean showsAnnotationOverview,
				int styles) {
			super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
			codeToOps.put(100, new GotoInkOperation());
			codeToOps.put(200, new GotoJavaOperation());
			codeToOps.put(300, new GenerateJavaOperation());
		}

		@Override
		public void doOperation(int operation) {
			if(operation>=100){
				InkEditorOperation op = codeToOps.get(operation);
				if(op!=null){
					try {

						boolean save = op.run(getDocument(), (TextSelection) getSelection(), getControl().getShell(), ((FileEditorInput)getEditorInput()).getFile());
						if(save){
							doSave(new NullProgressMonitor());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return;
				}
			}
			super.doOperation(operation);
		}

	}


}
