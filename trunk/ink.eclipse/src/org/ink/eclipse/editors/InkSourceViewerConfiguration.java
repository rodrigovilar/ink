package org.ink.eclipse.editors;


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.ink.eclipse.editors.highlight.InkScanner;
import org.ink.eclipse.editors.highlight.InkTagScanner;
import org.ink.eclipse.editors.partitioner.InkPartitionScanner;
import org.ink.eclipse.editors.processors.InkContentAssistProcessor;
import org.ink.eclipse.editors.rules.NonRuleBasedDamagerRepairer;
import org.ink.eclipse.editors.utils.ColorManager;
import org.ink.eclipse.editors.utils.InkColorConstants;

public class InkSourceViewerConfiguration extends TextSourceViewerConfiguration {

	private InkTagScanner tagScanner = null;
	private InkScanner scanner = null;
	private final ColorManager colorManager;

	public InkSourceViewerConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}


	protected InkScanner getInkScanner() {
		if (scanner == null) {
			scanner = new InkScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(InkColorConstants.DEFAULT))));
		}
		return scanner;
	}


	protected InkTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new InkTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(InkColorConstants.TAG))));
		}
		return tagScanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();


		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getInkScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		RuleBasedScanner multiLineScanner = new RuleBasedScanner();
        multiLineScanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(InkColorConstants.STRING))));
		DefaultDamagerRepairer dr2 = new DefaultDamagerRepairer(multiLineScanner);
        reconciler.setDamager(dr2, InkPartitionScanner.INK_STRING);
        reconciler.setRepairer(dr2, InkPartitionScanner.INK_STRING);

        TextAttribute textAttribute = new TextAttribute(
				colorManager.getColor(InkColorConstants.COMMENT));
		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
				textAttribute);
		reconciler.setDamager(ndr, InkPartitionScanner.INK_COMMENT);
		reconciler.setRepairer(ndr, InkPartitionScanner.INK_COMMENT);

		return reconciler;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(new InkContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(new InkContentAssistProcessor(), InkPartitionScanner.INK_STRING);
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		assistant.enablePrefixCompletion(true);
		assistant.enableAutoActivation(true);
		return assistant;

	}


	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
	{
	    return new String[]
	    {
	            IDocument.DEFAULT_CONTENT_TYPE,
	            InkPartitionScanner.INK_COMMENT,
	            InkPartitionScanner.INK_STRING,
	    };
	}

	/*public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
    {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_START_TAG);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_START_TAG);

        dr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_END_TAG);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_END_TAG);

        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_DOCTYPE);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_DOCTYPE);

        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_PI);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_PI);

        dr = new DefaultDamagerRepairer(getXMLTextScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_TEXT);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_TEXT);

        dr = new DefaultDamagerRepairer(getCDataScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_CDATA);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_CDATA);

        TextAttribute textAttribute = new TextAttribute(colorManager.getColor(IXMLColorConstants.XML_COMMENT));
        NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(textAttribute);
        reconciler.setDamager(ndr, XMLPartitionScanner.XML_COMMENT);
        reconciler.setRepairer(ndr, XMLPartitionScanner.XML_COMMENT);

        return reconciler;
    }*/



}
