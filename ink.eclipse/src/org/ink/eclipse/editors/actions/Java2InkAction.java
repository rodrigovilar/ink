package org.ink.eclipse.editors.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

public class Java2InkAction extends ActionDelegate implements IEditorActionDelegate{

	@Override
	public void run(IAction action) {
		ITextEditor te = (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().getActiveEditor();
		IEditorInput ei = te.getEditorInput();

		super.run(action);
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {

	}

}
