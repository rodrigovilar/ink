package org.ink.eclipse.editors.operations;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.utils.InkNotations;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.editors.page.DataBlock;
import org.ink.eclipse.editors.page.ObjectDataBlock;
import org.ink.eclipse.editors.page.PageAnalyzer;
import org.ink.eclipse.utils.InkUtils;

public abstract class InkEditorOperation {

	protected PageAnalyzer pa = null;

	public final boolean run(IDocument doc, TextSelection selection, Shell shell, IFile file) throws Exception {
		InkObject o = findInkObject(doc, selection);
		if (o != null) {
			return execute(o, shell, doc, file);
		}
		return false;
	}

	protected abstract boolean execute(InkObject o, Shell shell, IDocument doc, IFile file) throws Exception;

	protected boolean findTopLevelObjectOnly() {
		return false;
	}

	private InkObject findInkObject(IDocument doc, TextSelection selection) {
		InkObject o = null;
		String text = doc.get();
		String id;
		int offset = selection.getOffset();
		IEditorInput ei = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		IFile sourceFile = ((FileEditorInput) ei).getFile();
		String ns = InkUtils.resolveNamespace(sourceFile.getLocation().toFile());
		if (!findTopLevelObjectOnly()) {
			StringBuilder builder = new StringBuilder(50);
			for (int i = offset; i > 0; i--) {
				char c = text.charAt(i);
				if (c == '\n' || c == '\"') {
					break;
				}
				builder.append(c);
			}
			builder = builder.reverse();
			for (int i = offset + 1; i < text.length(); i++) {
				char c = text.charAt(i);
				if (c == '\n' || c == '\"') {
					break;
				}
				builder.append(c);
			}
			id = builder.toString();
			if (id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) < 0) {
				id = ns + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + id;
			}
			o = InkPlugin.getDefault().getInkContext().getFactory().getObject(id, false);
		}
		if (o == null) {
			int lineNumber = 0;
			try{
				lineNumber = doc.getLineOfOffset(offset);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			pa = new PageAnalyzer(ns, text, offset, lineNumber);
			ObjectDataBlock root = pa.getCurrentElement();
			if (root != null) {
				DataBlock element = root.getBlock(offset);
				if (element != null) {
					while (o == null && element != null) {
						if (element instanceof ObjectDataBlock) {
							id = ((ObjectDataBlock) element).getAttributeValue(InkNotations.Path_Syntax.ID_ATTRIBUTE);
							if (id == null) {
								id = ((ObjectDataBlock) element).getAttributeValue(InkNotations.Path_Syntax.CLASS_ATTRIBUTE);
							}
							if (id == null) {
								element = element.getParent();
							} else {
								o = InkPlugin.getDefault().getInkContext().getFactory().getObject(id, false);
							}
						} else {
							element = element.getParent();
						}
					}
				}
			}
		}
		return o;
	}

}
