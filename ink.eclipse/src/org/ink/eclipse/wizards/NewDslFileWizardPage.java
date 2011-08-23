package org.ink.eclipse.wizards;

import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.actions.RemoveBlockCommentAction;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ListDialog;
import org.ink.eclipse.utils.InkUtils;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (ink).
 */

public class NewDslFileWizardPage extends WizardPage {
	private Text containerText;

	private Text fileText;
	
	private ISelection selection;
	
	private Text classIdText;
	private Text namespaceText;
	private Text dslPackageText;
	private Text javaPackageText;
	private List importsListControl;
	private java.util.List<String>  importsList;
	private Button removeBtn;
	
	public String getClassId() {
		return classIdText.getText();
	}

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewDslFileWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Dsl File");
		setDescription("This wizard creates a new file with *.ink extension");
		this.selection = selection;
		
		importsList = new ArrayList<String>();
		
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.LEFT | SWT.WRAP);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
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
		label = new Label(container, SWT.LEFT | SWT.WRAP);;
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		label = new Label(container, SWT.LEFT | SWT.WRAP);;
		label.setText("&Class id:");
		
		classIdText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		classIdText.setLayoutData(gd);
		classIdText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		createLine(container, 3);
		
		label = new Label(container, SWT.NULL);
		label.setText("&Imports:");
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.verticalAlignment= GridData.BEGINNING;
		label.setLayoutData(gd);
		
		importsListControl = new List(container,
							   SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL 
							   | SWT.READ_ONLY);
		gd = new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace= false;
		gd.verticalAlignment= GridData.FILL;
		gd.grabExcessVerticalSpace= true;
		importsListControl.setLayoutData(gd);
		
		Composite buttons= getButtonBox(container);
		gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace= false;
		gd.verticalAlignment= GridData.FILL;
		gd.grabExcessVerticalSpace= true;
		gd.horizontalSpan= 1;
		buttons.setLayoutData(gd);
		
		label = new Label(container, SWT.LEFT | SWT.WRAP);;
		label.setText("&Namespace:");
		
		namespaceText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		namespaceText.setLayoutData(gd);
		namespaceText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		label = new Label(container, SWT.LEFT | SWT.WRAP);;
		label.setText("&Dsl package:");
		
		dslPackageText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		dslPackageText.setLayoutData(gd);
		dslPackageText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		label = new Label(container, SWT.LEFT | SWT.WRAP);;
		label.setText("&Java package:");
		
		javaPackageText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		javaPackageText.setLayoutData(gd);
		javaPackageText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		initialize();
		dialogChanged();
		setControl(container);
	}

	private void buildImportsList()
	{
		String containerName = getContainerName();
		if (containerName.length() != 0)
		{
			importsList.clear();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.findMember(new Path(containerName));
			
			// ToDo : Add validation check when user enter the container name
			// if (!resource.exists() || !(resource instanceof IContainer)) {
			// throwCoreException("Container \"" + containerName + "\" does not exist.");
			
			IContainer container = (IContainer) resource;
			IProject project = container.getProject();
			String[] dsls = InkUtils.getProjectDSLs(project); 
			Collections.addAll(importsList, dsls);
			try
			{
				IProject[] referencedProjects = project.getReferencedProjects();
				if (referencedProjects.length > 0)
				{
					for (IProject iProject : referencedProjects) 
					{
						dsls = InkUtils.getProjectDSLs(iProject); 
						Collections.addAll(importsList, dsls);
					}
				}
			}
			catch (CoreException e)
			{
				updateStatus("Error in getting referenced projects");
			}
						
		}
		
	}
	protected Button createButton(Composite parent, String label, SelectionListener listener) {
		Button button= new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(label);
		button.addSelectionListener(listener);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace= true;
		gd.verticalAlignment= GridData.BEGINNING;

		button.setLayoutData(gd);

		return button;
	}
	public Composite getButtonBox(Composite parent) 
	{
			
		Composite contents= new Composite(parent, SWT.NONE);
			
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		contents.setLayout(layout);

			
		Button button = new Button(contents, SWT.PUSH);
		button.setText("Add...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAddImports();
			}
		});
					
		//createSeparator(contents);
		
		removeBtn = new Button(contents, SWT.PUSH);
		removeBtn.setText("Remove...");
		removeBtn.setEnabled(false);
		removeBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleRemoveImports();
			}
		});


		return contents;
	}

	private void handleAddImports()
	{
		buildImportsList();
		ListDialog importsDialog = new ListDialog(getShell());
		importsDialog.setAddCancelButton(true);
		importsDialog.setContentProvider(new ArrayContentProvider());
		importsDialog.setLabelProvider(new LabelProvider());
		importsDialog.setInput(importsList.toArray());
		importsDialog.setInitialSelections(importsList.toArray());
		importsDialog.setTitle("Select dsl to import : ");
		if (importsDialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = importsDialog.getResult();
			if (result.length > 0)
			{
				for (Object resultItem : result)
				{
					if (!existInImportsList((String) resultItem))
					{
						importsListControl.add((String) resultItem);
					}	
				}
				removeBtn.setEnabled(true);			
			}
		}
	}
	private boolean existInImportsList(String itemToCheck)
	{
		String [] importsItems = importsListControl.getItems();
		for(String item : importsItems)
		{
			if(item.equals(itemToCheck))
				return true;
		}
		return false;
	}
	private void handleRemoveImports()
	{
		
		String[] selectedItems = importsListControl.getSelection();
		for (String item : selectedItems)
		{
			importsListControl.remove(item);
		}
		if (importsListControl.getItems().length == 0)
		{
			removeBtn.setEnabled(false);
		}
	
	}
	private Label createSeparator(Composite parent) {
		Label separator= new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setFont(parent.getFont());
		separator.setVisible(false);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.verticalAlignment= GridData.BEGINNING;
		gd.verticalIndent= 4;
		separator.setLayoutData(gd);
		return separator;
	}
	private void createLine(Composite parent, int ncol) 
	{
		Label line = new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL|SWT.BOLD);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = ncol;
		line.setLayoutData(gridData);
	}	
	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("dsls.ink");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
		
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();
		String classIdName = getClassId();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("ink") == false) {
				updateStatus("File extension must be \"ink\"");
				return;
			}
		}
		if (classIdName.length() == 0)
		{
			updateStatus("Class id must be specified");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
	
	public String getNamaspace() {
		return namespaceText.getText();
	}
	public String getDslPackage() {
		return dslPackageText.getText();
	}
	public String getJavaPackage() {
		return javaPackageText.getText();
	}
	
	public String[] getImportsList() {
		return importsListControl.getItems();
	}
}