package org.ink.eclipse.editors.operations;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Shell;
import org.ink.core.vm.lang.InkObject;
import org.ink.eclipse.utils.EclipseUtils;

public class GotoInkOperation extends InkEditorOperation{

	@Override
	protected boolean execute(InkObject o, Shell shell, IDocument doc, IFile file) {
		EclipseUtils.openEditor(o);
		return false;
	}

}
