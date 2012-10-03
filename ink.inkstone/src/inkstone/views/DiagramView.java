package inkstone.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import org.apache.batik.swing.JSVGCanvas;

public class DiagramView extends ViewPart {
	
	public static final String ID_ = "inkstone.views.DiagramView";

	private JSVGCanvas svgCanvas_;
	
	public DiagramView() {
		svgCanvas_ = new JSVGCanvas(null, true, true);
	}

	@Override
	public void createPartControl(Composite parent) {
		CLabel label = new CLabel(parent, SWT.DEFAULT);
		label.setText("INK Diagram View !");

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
