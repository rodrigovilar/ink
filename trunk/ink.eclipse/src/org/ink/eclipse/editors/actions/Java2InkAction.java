package org.ink.eclipse.editors.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;
import org.ink.core.vm.lang.InkObject;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.cache.Java2InkMappings;
import org.ink.eclipse.utils.EclipseUtils;

public class Java2InkAction extends ActionDelegate implements IEditorActionDelegate{

	@Override
	public void run(IAction action) {
		ITextEditor te = (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().getActiveEditor();
		IEditorInput ei = te.getEditorInput();
		IFile f = ((IFileEditorInput)ei).getFile();
		IPath p = EclipseUtils.getJavaCompiledClass(f.getProject(), f);
		String id = Java2InkMappings.get(p.toOSString());
		if(id!=null){
			InkObject o = InkPlugin.getDefault().getInkContext().getObject(id);
			if(o!=null){
				EclipseUtils.openEditor(o);
			}
		}

	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {

	}

}
