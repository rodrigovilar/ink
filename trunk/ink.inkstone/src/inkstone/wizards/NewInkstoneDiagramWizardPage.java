package inkstone.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewInkstoneDiagramWizardPage extends WizardPage {

	private ISelection selection_;
	private Text folderText_;
	private Text fileText_;
	
	public NewInkstoneDiagramWizardPage(ISelection selection) {
		super("New INK Diagram");
		setTitle("New INK Diagram - Create settings");
		setDescription("This wizard creates a new Inkstone diagram file with *.isd extension.");
		this.selection_ = selection;
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Dialog Folder:");

		folderText_ = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		folderText_.setLayoutData(gd);
		folderText_.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();				
			}
		});
		
		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("&Diagram file name:");

		fileText_ = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText_.setLayoutData(gd);
		fileText_.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
	}
	
	private void initialize() {
		if (selection_ != null && selection_.isEmpty() == false && selection_ instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection_;
			if (ssel.size() > 1) { return; }
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				folderText_.setText(container.getFullPath().toString());
			}
		}
		fileText_.setText("new_ink_diagram");
	}
	
	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), true,
				"Select a folder to place the new INK diagram.");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				folderText_.setText(((Path) result[0]).toString());
			}
		}
	}
	
	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember( new Path(getFolderName()) );
		String fileName = getFileName();
		
		if (getFolderName().length() == 0) {
			updateStatus("File folder must be specified !");
			return;
		}
		
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File folder must exist !");
			return;
		}
		
		if (!container.isAccessible()) {
			updateStatus("Project folder must be writable !");
			return;
		}
		
		if (fileName.length() == 0) {
			updateStatus("Diagram file name must be specified !");
			return;
		}
		
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid !");
			return;
		}
		
		updateStatus(null);
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String getFolderName() {
		return folderText_.getText();
	}

	public String getFileName() {
		return fileText_.getText();
	}

}
