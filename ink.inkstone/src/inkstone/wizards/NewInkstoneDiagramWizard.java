package inkstone.wizards;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewInkstoneDiagramWizard extends Wizard implements INewWizard {

	private NewInkstoneDiagramWizardPage page_;
	private ISelection selection_;
	
	public NewInkstoneDiagramWizard() {
		setWindowTitle("New INK Diagram");
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page_ = new NewInkstoneDiagramWizardPage(selection_);
		addPage(page_);
	}

	/**
	 * Accept the selection in the workbench to see if can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection_ = selection;
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
