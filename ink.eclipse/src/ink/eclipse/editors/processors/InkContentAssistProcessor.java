package ink.eclipse.editors.processors;

import ink.eclipse.editors.page.PageAnalyzer;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class InkContentAssistProcessor implements IContentAssistProcessor {

	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}

	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		String doc = viewer.getDocument().get();
		PageAnalyzer pageAnalyzer = new PageAnalyzer(doc, offset);
		List<ICompletionProposal> props = pageAnalyzer.getContentAssist();
		return props.toArray(new ICompletionProposal[]{});
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
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
