package org.ink.eclipse.editors.processors;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.ink.eclipse.editors.page.PageAnalyzer;
import org.ink.eclipse.utils.InkUtils;

public class InkContentAssistProcessor implements IContentAssistProcessor {

	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IEditorInput ei = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		if (ei instanceof FileEditorInput) {
			IFile sourceFile = ((FileEditorInput) ei).getFile();
			File f = sourceFile.getLocation().toFile();
			String doc = viewer.getDocument().get();
			int lineNumber = 0;
			try {
				lineNumber = viewer.getDocument().getLineOfOffset(offset);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			String ns = InkUtils.resolveNamespace(f);
			if (ns != null) {
				PageAnalyzer pageAnalyzer = new PageAnalyzer(ns, doc, offset, lineNumber);
				List<ICompletionProposal> props = pageAnalyzer.getContentAssist();
				return props.toArray(new ICompletionProposal[] {});
			}
		}
		return new ICompletionProposal[] {};
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { ' ', '\t' };
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}
