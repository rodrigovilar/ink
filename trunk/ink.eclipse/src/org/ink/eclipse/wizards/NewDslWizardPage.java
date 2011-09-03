package org.ink.eclipse.wizards;

import java.util.HashMap;
import java.util.Map;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaConventions;
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
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.ink.core.vm.factory.DslFactory;
import org.ink.eclipse.utils.InkUtils;

/**
 * The new DSL wizard page creates a new DSL declaration in a dsls.ink 
 * file for a specific project
 */

public class NewDslWizardPage extends WizardPage {
	private ISelection selection;
	
	
	// Wizard components
	private Text projectText;
	private Text namespaceText;
	private Text dslPackageText;
	private Text javaPackageText;
	private List importsListControl;
	private Button removeBtn;
	
	private Map<String, DslFactory> namespaceToDslFactoryMap;
	
	/**
	 * Constructor for NewDslWizardPage.
	 */
	public NewDslWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("New DSL");
		setDescription("This wizard creates a new DSL stamped to dsls.ink file");
		this.selection = selection;		
		namespaceToDslFactoryMap = new HashMap<String, DslFactory>();
		
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		
		// Creating main container
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		// Create project selection fields
		createLabel(container, "&Project:");
		projectText = createText(container, new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
				buildNamespaceToDslFactoryMap();
			}
		});
		projectText.setEditable(false);
		createButton(container, "Browse...", 
				new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		createLine(container, 3);
		
		// Create Imports dsls fields
		
		createLabel(container, "&Imports:");
		importsListControl = new List(container,
							   SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL 
							   | SWT.READ_ONLY);
		GridData gd = new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace= false;
		gd.verticalAlignment= GridData.FILL;
		gd.grabExcessVerticalSpace= true;
		importsListControl.setLayoutData(gd);
		importsListControl.setCapture(true);
		
		Composite buttons= getButtonBox(container);
		gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace= false;
		gd.verticalAlignment= GridData.FILL;
		gd.grabExcessVerticalSpace= true;
		gd.horizontalSpan= 1;
		buttons.setLayoutData(gd);
		
		// Create Namespace fields
		
		createLabel(container, "&Namespace:");
		namespaceText = createText(container,new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		createLabel(container,"");
		
		// Create DSL package fields
		createLabel(container, "&DSL package:");
		dslPackageText = createText(container, new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		createLabel(container,"");
		
		// Create Java package fields
		createLabel(container,"&Java package:");
		javaPackageText = createText(container, new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		createLabel(container,"");
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * This method gets all the project DSLS and their namespace(unique identifier)
	 * and builds a map between them
	 */
	private void buildNamespaceToDslFactoryMap()
	{
		String projectName = getProjectName();
		if (projectName.length() != 0)
		{
			namespaceToDslFactoryMap.clear();
			
			// Getting IPtoject from the project name string
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.findMember(new Path(projectName));
			IContainer container = (IContainer) resource;
			IProject project = container.getProject();
			
			// Getting the project DSLS 
			namespaceToDslFactoryMap = InkUtils.getProjectDSLFactories(project);
			
			try
			{
				// Getting the referenced projects DSLS
				IProject[] referencedProjects = project.getReferencedProjects();
				if (referencedProjects.length > 0)
				{
					for (IProject iProject : referencedProjects) 
					{
						namespaceToDslFactoryMap.putAll(InkUtils.getProjectDSLFactories(iProject));
					}
				}
			}
			catch (CoreException e)
			{
				updateStatus("Error in getting referenced projects");
			}
						
		}
		
	}
	
	/**
	 * Creating Text component
	 */
	protected Text createText(Composite parent, ModifyListener listener){
		Text txt = new Text(parent, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		txt.setLayoutData(gd);
		txt.addModifyListener(listener);
		return txt;
	}
	
	/**
	 * Creating Label component
	 */
	protected Label createLabel(Composite parent, String label) {
		Label lbl = new Label(parent, SWT.LEFT | SWT.WRAP);
		lbl.setText(label);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.verticalAlignment= GridData.BEGINNING;
		lbl.setLayoutData(gd);
		return lbl;
		
	}
	
	/**
	 * Creating Button component
	 */
	protected Button createButton(Composite parent, String label, SelectionListener listener) {
		Button button= new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(label);
		button.addSelectionListener(listener);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.verticalAlignment= GridData.BEGINNING;

		button.setLayoutData(gd);

		return button;
	}
	
	/**
	 * Creating Buttons box of "ADD" and "REMOVE"
	 */
	public Composite getButtonBox(Composite parent) 
	{
			
		Composite contents= new Composite(parent, SWT.NONE);
			
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		contents.setLayout(layout);

		createButton(contents, "Add...", 
				new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAddImports();
			}
		});
		
		removeBtn = createButton(contents, "Remove...",
				new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleRemoveImports();
			}
		});
		removeBtn.setEnabled(false);

		return contents;
	}

	/**
	 * This method is called when the Add button is entered(located next to the 
	 * imports list)
	 */
	private void handleAddImports()
	{
		// Opening ListSelectionDialog containing all the project DSLS
		ListSelectionDialog importsDialog = new ListSelectionDialog
				(getShell(),namespaceToDslFactoryMap.keySet(),
				 new ArrayContentProvider(), new LabelProvider(),
				 "DSL List");
		
		importsDialog.setTitle("Select DSL to import : ");
		
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
	
	/**
	 * This method checks duplicate items in the imports list component
	 */
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
	
	/**
	 * This method is called when the Remove button is entered (located next to the 
	 * imports list)
	 */
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
	/**
	 * This method creates a separator line in the wizard page
	 */
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
			
				projectText.setText(container.getFullPath().segment(0));
			}
		}
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the project field.
	 */
	
	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select destination project");
		
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				
				projectText.setText(((Path) result[0]).segment(0));
			}
		}
		
	}

	/**
	 * Ensures that all text fields are set.
	 */
	private void dialogChanged() {
		
		
		String dslPackageName = getDslPackage();
		String javaPackageName = getJavaPackage();
		
		if (!isProjectNameValid())
		{
			return;
		}
		
		if (!isNamespaceNameValid())
		{
			return;
		}
		
		if (dslPackageName.length() == 0)
		{
			updateStatus("DSL package must be specified");
			return;
		}
		
		if (!isPackageNameValid(dslPackageName))
		{
			return;
		}
	
		if (isDslPackageExist())
		{
			updateStatus("DSL package is already exist");
			return;
		}
		
		if (javaPackageName.length() == 0)
		{
			updateStatus("Java package must be specified");
			return;
		}
		
		if (!isPackageNameValid(javaPackageName))
		{
			return;
		}
		
		if (isJavaPackageExist())
		{
			updateStatus("Java package is already exist");
			return;
		}
		updateStatus(null);
	}

	/**
	 * Ensures that DSL project is valid
	 */
	private boolean isProjectNameValid()
	{
		IResource project = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getProjectName()));
		
		if (getProjectName().length() == 0) {
			updateStatus("DSL project must be specified");
			return false;
		}
		if (project == null
				|| (project.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("DSL project must exist");
			return false;
		}
		if (!project.isAccessible()) {
			updateStatus("Project must be writable");
			return false;
		}
		return true;
	}
	
	/**
	 * Ensures that DSL namespace is valid
	 */
	private boolean isNamespaceNameValid()
	{
		String namespaceName = getNamaspace();
		if (namespaceName.length() == 0)
		{
			updateStatus("Namespace must be specified");
			return false;
		}
		if (!namespaceName.matches("^[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$"))
		{
			updateStatus("Namespace format isn't valid.\n" +
						 "Should match [alpha/digit].[alpha/digit]" );
			
			return false;
		}
		if (namespaceToDslFactoryMap.keySet().contains(namespaceName))
		{
			updateStatus("Namespace is already exist");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Ensures that DSL/Java package is valid
	 */
	private boolean isPackageNameValid(String packageName)
	{
		IStatus packageNameStat = JavaConventions.validatePackageName(packageName, "1.6", "1.6");
		if(packageNameStat.getSeverity()== IStatus.ERROR)
		{
			updateStatus(packageNameStat.getMessage());
			return false;
		}
		return true;
	}
	private boolean isDslPackageExist()
	{
		for (Map.Entry<String, DslFactory> en : namespaceToDslFactoryMap.entrySet())
		{
			DslFactory dslFactory = en.getValue();
			if (dslFactory.getDslPackage().equals(getDslPackage()))
			{
				return true;
			}
		}
		return false;
	}
	
	boolean isJavaPackageExist()
	{
		for (Map.Entry<String, DslFactory> en : namespaceToDslFactoryMap.entrySet())
		{
			DslFactory dslFactory = en.getValue();
			if (dslFactory.getJavaPackage().equals(getJavaPackage()))
			{
				return true;
			}
		}
		return false;
	}
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getProjectName() {
		return projectText.getText();
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
	
	public Map<String, DslFactory> getNamespaceToDslFactoryMap()
	{
		return namespaceToDslFactoryMap;
	}
}