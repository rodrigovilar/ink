package inkstone.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import inkstone.models.InkstoneLibrary;
import inkstone.models.InkstoneProject;
import inkstone.models.dslLibsLabelProvider;
import inkstone.models.dslLibsTreeContentProvider;
import inkstone.utils.InkstoneGallery;
import inkstone.utils.KioskOverloadOfElements;
import inkstone.utils.KioskOverloadOfVisualElements;
import inkstone.views.KioskView;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TreeItem;


/**
 * A {@link TitleAreaDialog} extension class for selecting of DSL namespaces from an ink-projects tree.
 * 
 * @author Ofer Calvo 
 */
public class DSLsSelectionDialog extends TitleAreaDialog {

	// GUI widgets & viewers
	private CheckboxTreeViewer cbTreeViewer_;
	private Button okButton_;
	private Button cancelButton_;
	private Button selectAllButton_;
	private Button deselectAllButton_;
	private KioskView kiosk_;

	
	/** 
	 * The dialogs output variable. should contain user selected ink (descriptive) model.
	 */
	private List<InkstoneProject> selectedInktoneModel_;
	
	private List<String> lastSelectedDSLs_;
	
	// dialog results (OK
	public static final int DONE = 0;
	public static final int CANCEL = 1;  
	
	/**
	 * Comparator class for sorting {@link InkstoneProject} by their project names.
	 */
	public class InkstoneProjectComparable implements Comparator<InkstoneProject>{
	    @Override
	    public int compare(InkstoneProject p1, InkstoneProject p2) {
	        return (p1.getName().compareTo(p2.getName()));
	    }
	}
	
	/**
	 * The dialogs constructor. Sets help button and the dialog title image.
	 * @param parentShell the parent SWT shell (pass to the super class).
	 */
	public DSLsSelectionDialog(Shell parentShell, KioskView kiosk) {
		super(parentShell);
		setHelpAvailable(false);	// Set not to show the help button.
		setBlockOnOpen(true); 		// set dialog to be modal (open method blocks)
		// Set title image
		setTitleImage(InkstoneGallery.getInstance().getImage(InkstoneGallery.INKSTONE_MED_LOGO));
		this.kiosk_ = kiosk;
		lastSelectedDSLs_ = new ArrayList<String>();
	}
	
	@Override
	public void create() {
		super.create();
		// Set the title and first message to the user
		setTitle("DSL's Selection Dialog");
		// if tree viewer not empty place regular instruction message.
		if( cbTreeViewer_.getTree().getItems().length > 0) {
			setMessage("Please select DSL libraries (namespaces),\n from active INK Projects.", IMessageProvider.INFORMATION);
	    }
		else {
			// if tree viewer is empty alert the user and disable the 'Select' button.
			setMessage("No active INK Projects found.\n Selection not possible !", IMessageProvider.WARNING);
			okButton_.setEnabled(false);
		}		
	}
	
	@Override
	public boolean close() {
		if( okButton_ != null ) {
			okButton_.dispose();
		}
		if( deselectAllButton_ != null ) {
			deselectAllButton_.dispose();
		}
		if( cancelButton_ != null ) {
			deselectAllButton_.dispose();
		}
		return super.close();
	}
	
	@Override
	protected void configureShell(Shell shell)	{
		super.configureShell(shell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// Define sub composite and set layout
		Composite treeComposite = new Composite(parent, SWT.NONE );
		treeComposite.setLayout(new GridLayout());
		treeComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		// Define the check-box tree viewer 
	    cbTreeViewer_ = new CheckboxTreeViewer(treeComposite, SWT.BORDER);
	    cbTreeViewer_.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    try {
			cbTreeViewer_.setContentProvider(new dslLibsTreeContentProvider(kiosk_));
		} catch (KioskOverloadOfVisualElements e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    cbTreeViewer_.setLabelProvider(new dslLibsLabelProvider());
	    cbTreeViewer_.setAutoExpandLevel(2);
	    cbTreeViewer_.setInput("root");
	    cbTreeViewer_.addCheckStateListener(new ICheckStateListener() {
	    	// Handle a change to the checked state of an element and their parent/child's state
			public void checkStateChanged(CheckStateChangedEvent event) { 
				if (event.getChecked()) {
					// Parent node check all its child nodes
					if( event.getElement() instanceof InkstoneProject ) {
						cbTreeViewer_.setSubtreeChecked(event.getElement(), true);
					}
					// Child node checks its parent
					if( event.getElement() instanceof InkstoneLibrary ) {
						cbTreeViewer_.setChecked(((InkstoneLibrary)event.getElement()).getProject(), true);
					}
				}
				else {
					// Parent node unchecked all its child nodes
					if( event.getElement() instanceof InkstoneProject ) {
						cbTreeViewer_.setSubtreeChecked(event.getElement(), false);
					}
				}
			}
		});
	    
	    Composite buttonsComposite = new Composite(parent, SWT.NONE );
	    buttonsComposite.setLayout(new GridLayout(2, true));
	    buttonsComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
	        
	 // Create select-all button
	    selectAllButton_ = new Button(buttonsComposite, SWT.NONE);
	    selectAllButton_.setText("Select all");
	    selectAllButton_.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		checkAllTreeNodes(true);
	    	}
	    });
	    
	    // Create deselect-all button
	    deselectAllButton_ = new Button(buttonsComposite, SWT.NONE);
	    deselectAllButton_.setText("Deselect all");
	    deselectAllButton_.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		checkAllTreeNodes(false);
	    	}
	    });
	    
	    restoreLastSelections();
	    setErrorMessage(null);
	    
	    return parent;
	}
	
	private void checkAllTreeNodes(boolean checkedVal) {
		TreeItem[] treeItemProjects = cbTreeViewer_.getTree().getItems();
 		for( int i = 0; i < treeItemProjects.length; i++) {
 			treeItemProjects[i].setChecked(checkedVal);
 			TreeItem[] treeItemDSLs = treeItemProjects[i].getItems();
			for (int j = 0; j < treeItemDSLs.length; j++) {
				treeItemDSLs[j].setChecked(checkedVal);
			}
 		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
	    gridData.verticalAlignment = GridData.FILL;
	    gridData.horizontalSpan = 3;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.horizontalAlignment = SWT.RIGHT;
	    parent.setLayoutData(gridData);
	    
	    // Create DONE button with custom method, cause we need to overview the SelectionAdapter
	    okButton_ = createSelectButton(parent, DSLsSelectionDialog.DONE, "Done", true);
	    okButton_.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
		        }
			}
		});
	    	    
	    // Create CANCEL button
	    cancelButton_ = createButton(parent, DSLsSelectionDialog.CANCEL, "Cancel", false);
	    cancelButton_.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
				setReturnCode(DSLsSelectionDialog.CANCEL);
				close();
	      	}
	    });
	}
	
	protected Button createSelectButton(Composite parent, int id, String label, boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}
	
	private boolean isValidInput() {
		boolean valid = true;
		if(cbTreeViewer_.getCheckedElements().length == 0) {
			setErrorMessage("First select at least one DSL namespaces.");
			valid = false;
		}
	    return valid;
	}
	  
	@Override
	protected boolean isResizable() {
		return false;
	}
 
	@Override
	protected void okPressed() {
		try {
			saveInput();
		} catch (KioskOverloadOfVisualElements e) {
			// Not handled here. See the KioskView 'drawInKiosk' method.
		} catch (KioskOverloadOfElements e) {
			KioskOverloadOfElementsMessgae(e.getMessage());
			if( selectedInktoneModel_.size() > 0 ) {
				for( InkstoneProject project : selectedInktoneModel_) {
		 			project.dispose();
				}
			}
			selectedInktoneModel_.clear();
		}
		super.okPressed();
	}

	/**
	 * @return the selected (sub) Inkstone model (reflects the ink model).
	 */
	public List<InkstoneProject> getSelectedInkstoneModel() {
		return selectedInktoneModel_;
	}
	
	// 
	/**
	 * Copy selections to persistent data model variable.
	 * @throws KioskOverloadOfVisualElements 
	 * 
	 * @see selectedInktoneProjects_
	 */
 	private void saveInput() throws KioskOverloadOfVisualElements, KioskOverloadOfElements { 
		if( cbTreeViewer_.getCheckedElements().length == 0 ) {
			selectedInktoneModel_ = null;
			return;
		}
		lastSelectedDSLs_.clear();
		selectedInktoneModel_ = new ArrayList<InkstoneProject>();
		TreeItem[] treeItemProjects = cbTreeViewer_.getTree().getItems();
		for( int i = 0; i < treeItemProjects.length; i++) {
			int dslCount = 0;
			if( treeItemProjects[i].getChecked() ) {
				InkstoneProject tempInkstoneProject = new InkstoneProject(treeItemProjects[i].getText(), kiosk_);
				TreeItem[] treeItemDSLs = treeItemProjects[i].getItems();
				for (int j = 0; j < treeItemDSLs.length; j++) {
					if( treeItemDSLs[j].getChecked() ) {
						tempInkstoneProject.addDslLib(treeItemDSLs[j].getText());
						dslCount++;
						String id = new String(treeItemProjects[i].getText() + "|" + treeItemDSLs[j].getText());
						lastSelectedDSLs_.add(id);
					}
				}
				if( dslCount > 0 ) {
					selectedInktoneModel_.add(tempInkstoneProject);
					if( getSelectedInktoneModelElementsCount() > KioskView.maxAllowedElement_ ) {
						throw new KioskOverloadOfElements("Maximum allowed elements reached the upper barrier limit (currently set to " + KioskView.maxAllowedElement_ + "). Select less DSLs. Or, change Kiosk configuration at the Inkstone preferences.");
					}
				}
			}
		}
		Collections.sort(selectedInktoneModel_, Collections.reverseOrder(new InkstoneProjectComparable()));
	}
 	
 	private int getSelectedInktoneModelElementsCount() {
 		
 		if( selectedInktoneModel_ == null ) { return 0; }
 		else if( selectedInktoneModel_.isEmpty()) { return 0; }
 		
 		int elementsSum = 0;
 		if( selectedInktoneModel_.size() > 0 ) {
	 		for( InkstoneProject project : selectedInktoneModel_) {
	 			elementsSum += project.getElementsCount();
			}
 		}
 		return elementsSum;
 	}
 	
 	/**
 	 * Restores last tree check-boxes selections from previous run of the dialog. 
 	 */
 	private void restoreLastSelections() {
 		int index;
 		Collections.sort(lastSelectedDSLs_);
 		TreeItem[] treeItemProjects = cbTreeViewer_.getTree().getItems();
 		for( int i = 0; i < treeItemProjects.length; i++) {
 			TreeItem[] treeItemDSLs = treeItemProjects[i].getItems();
			for (int j = 0; j < treeItemDSLs.length; j++) {
				String id = new String(treeItemProjects[i].getText() + "|" + treeItemDSLs[j].getText());
				index = Collections.binarySearch(lastSelectedDSLs_, id);
				if(index >= 0) {
					treeItemDSLs[j].setChecked(true);
					treeItemProjects[i].setChecked(true);
				}
			}
 		}
 	}
 	
	private void KioskOverloadOfElementsMessgae(String msg) {
		MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_ERROR);
		messageBox.setText("InkStone Kiosk error !");
		messageBox.setMessage(msg);
		messageBox.open();
	}
 	 	
}
