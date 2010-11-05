package ink.eclipse.editors;


import ink.eclipse.editors.document.InkDocumentProvider;
import ink.eclipse.editors.utils.ColorManager;

import org.eclipse.ui.editors.text.TextEditor;

public class InkEditor extends TextEditor{
	
	private ColorManager colorManager;
	
	public InkEditor() {
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new InkConfiguration(colorManager));
		setDocumentProvider(new InkDocumentProvider());
		
	}

	
}
