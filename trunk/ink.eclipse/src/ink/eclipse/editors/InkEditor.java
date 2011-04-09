package ink.eclipse.editors;


import ink.eclipse.editors.document.InkDocumentProvider;
import ink.eclipse.editors.page.DataBlock;
import ink.eclipse.editors.page.ObjectDataBlock;
import ink.eclipse.editors.page.PageAnalyzer;
import ink.eclipse.editors.utils.ColorManager;

import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.utils.InkNotations;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.EclipseUtils;
import org.ink.eclipse.utils.InkUtils;

public class InkEditor extends TextEditor{

	public static final String EDITOR_ID = "ink.eclipse.editors.InkEditor";

/*	public static final String EDITOR_CONTEXT = EDITOR_ID + ".context";

	public static final String RULER_CONTEXT = EDITOR_CONTEXT + ".ruler";

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT);
		setRulerContextMenuId(RULER_CONTEXT);
	}
*/
	private final ColorManager colorManager;

	public InkEditor() {
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new InkConfiguration(colorManager));
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

		TextOperationAction action= new TextOperationAction(ResourceBundle.getBundle(InkMessages.class.getName()),"Goto Ink Element.", this, 100, true);
		action.setText("Goto Ink Element");
		action.setToolTipText("Goto Ink Element");
		action.setActionDefinitionId("ink.eclipse.gotoElement");
		action.setEnabled(true);
		setAction("in.eclipse.gotoElement", action);

		action= new TextOperationAction(ResourceBundle.getBundle(InkMessages.class.getName()),"Goto Java.", this, 200, true);
		action.setText("Goto Java");
		action.setToolTipText("Goto Java");
		action.setActionDefinitionId("ink.eclipse.gotoJava");
		action.setEnabled(true);
		setAction("ink.eclipse.gotoJava", action);
	}

	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		menu.insertAfter(IContextMenuConstants.GROUP_OPEN, new GroupMarker(IContextMenuConstants.GROUP_SHOW));
		IAction action= getAction("ink.eclipse.gotoElement");
		action.setEnabled(true);
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);

		menu.insertAfter(IContextMenuConstants.GROUP_OPEN, new GroupMarker(IContextMenuConstants.GROUP_SHOW));
		action= getAction("ink.eclipse.gotoJava");
		action.setEnabled(true);
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);
	}

	public class InkSourceViewer extends ProjectionViewer{

		public InkSourceViewer(Composite parent, IVerticalRuler ruler,
				IOverviewRuler overviewRuler, boolean showsAnnotationOverview,
				int styles) {
			super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
		}

		@Override
		public void doOperation(int operation) {
			if(operation>=100){
				String text = getDocument().get();
				int offset = ((TextSelection)getSelection()).getOffset();
				StringBuilder builder = new StringBuilder(50);
				for(int i=offset;i>0;i--){
					char c = text.charAt(i);
					if(c=='\n' || c=='\"'){
						break;
					}
					builder.append(c);
				}
				builder = builder.reverse();
				for(int i=offset+1;i<text.length();i++){
					char c = text.charAt(i);
					if(c=='\n' || c=='\"'){
						break;
					}
					builder.append(c);
				}
				String id = builder.toString();
				IEditorInput ei = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
				IFile sourceFile = ((FileEditorInput) ei).getFile();
				String ns = InkUtils.resolveNamespace(sourceFile.getLocation().toFile());
				if(id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) < 0){
					id = ns + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + id;
				}
				InkObject o = InkPlugin.getDefault().getInkContext().getFactory().getObject(id, false);
				if(o==null){
					PageAnalyzer pa = new PageAnalyzer(ns, text, offset);
					DataBlock element = pa.getCurrentElement().getBlock(offset);
					if(element!=null){
						while(o==null && element!=null){
							if(element instanceof ObjectDataBlock){
								id = ((ObjectDataBlock)element).getAttributeValue(InkNotations.Path_Syntax.ID_ATTRIBUTE);
								if(id==null){
									id = ((ObjectDataBlock)element).getAttributeValue(InkNotations.Path_Syntax.CLASS_ATTRIBUTE);
								}
								if(id==null){
									element = element.getParent();
								}else{
									o = InkPlugin.getDefault().getInkContext().getFactory().getObject(id, false);
								}
							}else{
								element = element.getParent();
							}
						}
					}
				}
				if(o!=null){
					switch (operation) {
					case 100:
						EclipseUtils.openEditor(o);
						return;
					case 200:
						EclipseUtils.openJava(o);
						return;
					}
				}
			}
			super.doOperation(operation);
		}

	}


}
