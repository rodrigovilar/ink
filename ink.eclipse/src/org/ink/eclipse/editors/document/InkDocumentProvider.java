package org.ink.eclipse.editors.document;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.ink.eclipse.editors.partitioner.InkPartitionScanner;
import org.ink.eclipse.editors.partitioner.InkPartitioner;

public class InkDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner = new InkPartitioner(new InkPartitionScanner(), new String[] { InkPartitionScanner.INK_COMMENT, InkPartitionScanner.INK_STRING, });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}