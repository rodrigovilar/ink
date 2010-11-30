package ink.eclipse.editors;


import ink.eclipse.editors.document.InkDocumentProvider;
import ink.eclipse.editors.utils.ColorManager;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;

public class InkEditor extends TextEditor{

	private final ColorManager colorManager;

	public InkEditor() {
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new InkConfiguration(colorManager));
		setDocumentProvider(new InkDocumentProvider());

	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(Composite, IVerticalRuler, int)
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {

		fAnnotationAccess= getAnnotationAccess();
		fOverviewRuler= createOverviewRuler(getSharedColors());

		ISourceViewer viewer= new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}


}
