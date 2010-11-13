package ink.eclipse.editors;

import ink.eclipse.editors.highlight.InkScanner;
import ink.eclipse.editors.highlight.InkTagScanner;
import ink.eclipse.editors.partitioner.InkPartitionScanner;
import ink.eclipse.editors.processors.InkContentAssistProcessor;
import ink.eclipse.editors.rules.NonRuleBasedDamagerRepairer;
import ink.eclipse.editors.utils.ColorManager;
import ink.eclipse.editors.utils.InkColorConstants;

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
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class InkConfiguration extends SourceViewerConfiguration {

	//private XMLDoubleClickStrategy doubleClickStrategy;

	private InkTagScanner tagScanner;

	private InkScanner scanner;

	//private XMLTextScanner textScanner;

	private ColorManager colorManager;

	public InkConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				InkPartitionScanner.INK_COMMENT,
				InkPartitionScanner.INK_STRING, };
	}

	// public ITextDoubleClickStrategy getDoubleClickStrategy(
	// ISourceViewer sourceViewer, String contentType) {
	// if (doubleClickStrategy == null)
	// doubleClickStrategy = new XMLDoubleClickStrategy();
	// return doubleClickStrategy;
	// }

	protected InkScanner getInkScanner() {
		if (scanner == null) {
			scanner = new InkScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(InkColorConstants.DEFAULT))));
		}
		return scanner;
	}

//	protected XMLTextScanner getXMLTextScanner() {
//		if (textScanner == null) {
//			textScanner = new XMLTextScanner(colorManager);
//			textScanner.setDefaultReturnToken(new Token(new TextAttribute(
//					colorManager.getColor(InkColorConstants.DEFAULT))));
//		}
//		return textScanner;
//	}

	protected InkTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new InkTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(InkColorConstants.TAG))));
		}
		return tagScanner;
	}

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

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(new InkContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(new InkContentAssistProcessor(), InkPartitionScanner.INK_STRING);
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		return assistant;

	}

	/*
	 * public IContentFormatter getContentFormatter(ISourceViewer sourceViewer)
	 * { ContentFormatter formatter = new ContentFormatter();
	 * XMLFormattingStrategy formattingStrategy = new XMLFormattingStrategy();
	 * DefaultFormattingStrategy defaultStrategy = new
	 * DefaultFormattingStrategy(); TextFormattingStrategy textStrategy = new
	 * TextFormattingStrategy(); DocTypeFormattingStrategy doctypeStrategy = new
	 * DocTypeFormattingStrategy(); PIFormattingStrategy piStrategy = new
	 * PIFormattingStrategy(); formatter.setFormattingStrategy(defaultStrategy,
	 * IDocument.DEFAULT_CONTENT_TYPE);
	 * formatter.setFormattingStrategy(textStrategy,
	 * XMLPartitionScanner.XML_TEXT);
	 * formatter.setFormattingStrategy(doctypeStrategy,
	 * XMLPartitionScanner.XML_DOCTYPE);
	 * formatter.setFormattingStrategy(piStrategy, XMLPartitionScanner.XML_PI);
	 * formatter.setFormattingStrategy(textStrategy,
	 * XMLPartitionScanner.XML_CDATA);
	 * formatter.setFormattingStrategy(formattingStrategy,
	 * XMLPartitionScanner.XML_START_TAG);
	 * formatter.setFormattingStrategy(formattingStrategy,
	 * XMLPartitionScanner.XML_END_TAG);
	 * 
	 * return formatter; }
	 */
}