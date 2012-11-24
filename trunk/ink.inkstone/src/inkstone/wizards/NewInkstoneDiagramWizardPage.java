package inkstone.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.SaveAsDialog;
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
	private Text diagramFileText_;
	private Text inkFileText_;
	private IPath diagramFolderPath_;
	private IPath inkFilePath_;
	
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
		label.setText("Diagram file name:");

		diagramFileText_ = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		diagramFileText_.setLayoutData(gd);
		diagramFileText_.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		Label filler= new Label(container, SWT.LEFT);
		filler.setText(" ");
		
		label = new Label(container, SWT.NULL);
		label.setText("Dialog Folder:");

		folderText_ = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		folderText_.setLayoutData(gd);
		folderText_.setEditable(false);
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
				handleFolderBrowse();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("Default saves INK file:");
	
		inkFileText_ = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		inkFileText_.setLayoutData(gd);
		inkFileText_.setEditable(false);
		inkFileText_.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();				
			}
		});
		
		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleFileBrowse();
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
				diagramFolderPath_ = container.getLocation();
				folderText_.setText(container.getFullPath().toString());
			}
		}
		diagramFileText_.setText("new_ink_diagram");
	}
	
	private void handleFolderBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), true,
				"Select an INK file to contain the diagram new future elements.");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				diagramFolderPath_ = (IPath)result[0];
				folderText_.setText(diagramFolderPath_.toOSString());
			}
		}
	}

	private void handleFileBrowse() {
		SaveAsDialog dialog = new SaveAsDialog(getShell());
		dialog.setOriginalName(diagramFileText_.getText() + ".ink");
		dialog.open();
		inkFilePath_ = dialog.getResult();
		if( inkFilePath_ != null ) {
			inkFileText_.setText(inkFilePath_.toString());
		}
	}

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember( new Path(getDiagramFolderName()) );
		String fileName = getDiagramFileName();
		
		if (fileName.length() == 0) {
			updateStatus("Diagram file name must be specified !");
			return;
		}
		
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid !");
			return;
		}
		
		if (getDiagramFolderName().length() == 0) {
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
		
		if(getInkFileName().length() == 0) {
			updateStatus("INK File must be specified !");
			return;
		}
		
		updateStatus(null);
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String getDiagramFolderName() {
		return folderText_.getText();
	}
	
	public IPath getDiagramFolderPath() {
		return diagramFolderPath_;
	}

	public String getDiagramFileName() {
		return diagramFileText_.getText();
	}
	
	public IPath getDiagramFilePath() {
		return new Path(getDiagramFileName());
	}
	
	public String getInkFileName() {
		return inkFileText_.getText();
	}
	
	public IPath getInkFilePath() {
		return inkFilePath_;
	}

}
