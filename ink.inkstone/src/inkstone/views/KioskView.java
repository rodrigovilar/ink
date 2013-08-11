package inkstone.views;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import inkstone.dialogs.DSLsSelectionDialog;
import inkstone.models.InkstoneElement;
import inkstone.models.InkstoneElementKind;
import inkstone.models.InkstoneLibrary;
import inkstone.models.InkstoneProject;
import inkstone.preferences.InkstonePreferenceConstants;
import inkstone.utils.InkstoneGallery;
import inkstone.utils.KioskOverloadOfVisualElementsException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.app.Messages;
import org.eclipse.jface.action.Action;  
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;  
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction; 

/**
 * An Eclipse SDK view part implemented for the InkStone plugin.
 * Lets the user filter, find and select ink elements in a visual graphic interface.
 *  
 * @author Ofer Calvo
 */
public class KioskView extends ViewPart {
	
	public static final String ID_ = "inkstone.views.KioskView";
	public static int maxAllowedElement_ = 
			inkstone.Activator.getDefault().getPreferenceStore().getInt(InkstonePreferenceConstants.MAX_KIOSK_ALLOWED_ELEMENTS);
	public static int maxViewedElement_ = 
			inkstone.Activator.getDefault().getPreferenceStore().getInt(InkstonePreferenceConstants.MAX_KIOSK_VISUAL_ELEMENTS);
	
	private final Display display_ = Display.getCurrent();
	private final Shell shell_ = new Shell(display_);
	private final InkstoneGallery inkGallery_;
	private Menu popupMenu_;
	private ExpandBar mainExpBar_;
	private Text filterTextbox_;
	private Button filterButton_;
	private DSLsSelectionDialog dslsSelectionDialog_;
	private MenuDetectListener popupMenuListener_;
	private List<InkstoneProject> inkstoneModel_;
	private InkstoneElement selectedElement_;
	private boolean hideInkNotations_;

	/**
	 * Inner class to handle custom action of INK notations bar show/hide action.
	 */
	private class ShowHideInkNotationsAction extends Action implements IWorkbenchAction{
		public ShowHideInkNotationsAction() {
			super("Show/Hide the INK Notations bar", Action.AS_CHECK_BOX);
		}
		
		@Override
		public void run() {
			showHideInkNotations(this.isChecked());
		}
		
		public void dispose() {}
	}
	
	/**
	 * Inner class to handle custom action of INK namespaces refresh.
	 */
	private class RefreshInkNamespacesAction extends Action implements IWorkbenchAction{
		@Override
		public void run() {
			refreshModelExpandBars();
		}

		@Override
		public void dispose() {}
	}
	
	/**
	 * Inner class to handle custom action of INK namespaces selection.
	 */
	private class SelectInkNamespacesAction extends Action implements IWorkbenchAction{
		@Override
		public void run() {
			openDSLsSelectionDialog();
		}
		
		public void dispose() {}
	}
	
	/**
	 * Inner class to handle custom action of INK namespaces deletion.
	 */
	private class DeleteInkNamespacesAction extends Action implements IWorkbenchAction{
		@Override
		public void run() {
			clearModelExpandBars();
		}

		@Override
		public void dispose() {}
	}
	
	public KioskView() {
		this.inkGallery_ = InkstoneGallery.getInstance();
		this.selectedElement_ = null;
		this.hideInkNotations_ = true;
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();  
		dslsSelectionDialog_ = new DSLsSelectionDialog(shell, this); 
		dslsSelectionDialog_.create();
		
	}

	@Override
	public void createPartControl(Composite parent) {

		popupMenu_ = new Menu(shell_, SWT.POP_UP);
		initPopupMenu();
		
		//Define the show/hide ink notations expand bar
		ShowHideInkNotationsAction customShowHideInkNotationsAction = new ShowHideInkNotationsAction();
		customShowHideInkNotationsAction.setImageDescriptor(inkGallery_.getImageDescriptor(InkstoneGallery.PAINT_ICON));
		customShowHideInkNotationsAction.setChecked(!hideInkNotations_);
		getViewSite().getActionBars().getToolBarManager().add(customShowHideInkNotationsAction);
		
		// Define the refress kiosk custom action in the view's Menu
		RefreshInkNamespacesAction customActionRefreshInkNamespaces = new RefreshInkNamespacesAction();
		customActionRefreshInkNamespaces.setText("Refresh Kiosk data");
		customActionRefreshInkNamespaces.setImageDescriptor(inkGallery_.getImageDescriptor(InkstoneGallery.GENERAL_REFRESH_ICON));
		getViewSite().getActionBars().getToolBarManager().add(customActionRefreshInkNamespaces);

		// Define ADD/SELECT Ink namespaces custom action in the view's Menu  
		SelectInkNamespacesAction customActionSelectInkNamespaces = new SelectInkNamespacesAction();  
		customActionSelectInkNamespaces.setText("Open DSL namespaces selection dialog");  
		customActionSelectInkNamespaces.setImageDescriptor(inkGallery_.getImageDescriptor(InkstoneGallery.GENERAL_ADD_ICON));
		getViewSite().getActionBars().getToolBarManager().add(customActionSelectInkNamespaces);
		
		// Define DELETE Ink namespaces custom action in the view's Menu
		DeleteInkNamespacesAction customActionDeleteInkNamespaces =  new DeleteInkNamespacesAction();
		customActionDeleteInkNamespaces.setText("Clear INK model elements from Kiosk view");
		customActionDeleteInkNamespaces.setImageDescriptor(inkGallery_.getImageDescriptor(InkstoneGallery.GENERAL_DELETE_ICON));
		getViewSite().getActionBars().getToolBarManager().add(customActionDeleteInkNamespaces);
		
		FormLayout layout= new FormLayout();
		parent.setLayout(layout);
		
		filterTextbox_ = new Text(parent, SWT.SINGLE | SWT.BORDER );
		filterButton_ = new Button(parent, SWT.TOGGLE );

		mainExpBar_ = new ExpandBar (parent, SWT.V_SCROLL);
		
		FormData filterTextData = new FormData();
		filterTextData.left = (new FormAttachment(0, 0));
		filterTextData.right = (new FormAttachment(90, 0));
		filterTextData.top = (new FormAttachment(0,0));
		filterTextData.bottom = (new FormAttachment(mainExpBar_,-2));
		filterTextbox_.setLayoutData(filterTextData);
		filterTextbox_.setText(".*");
		filterTextbox_.setToolTipText("Filter by regular expression syntax.\nUse Enter/Esc keys to Start/Stop filtering display.");
		filterTextbox_.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(inkstoneModel_==null) {return;}
				if(e.character == SWT.CR) {
					if(inkstoneModel_.size()==0) {return;}
					filterButton_.setImage(inkGallery_.getImage(InkstoneGallery.GENERAL_CANCEL_ICON));
					filterButton_.setSelection(true);
					filterTextbox_.setBackground(display_.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
					filterKioskBars(filterTextbox_.getText());
				}
				if(e.character == SWT.ESC) {
					filterButton_.setImage(inkGallery_.getImage(InkstoneGallery.GENERAL_SEARCH_ICON));
					filterButton_.setSelection(false);
					filterTextbox_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
					filterTextbox_.setText(".*");
					filterKioskBars(filterTextbox_.getText());
				}
			}
		});
		
		FormData filterButtonData = new FormData();
		filterButtonData.left = (new FormAttachment(filterTextbox_, 2));
		filterButtonData.right = (new FormAttachment(100, 0));
		filterButton_.setLayoutData(filterButtonData);
		filterButton_.setSelection(false);
		filterButton_.setImage(inkGallery_.getImage(InkstoneGallery.GENERAL_SEARCH_ICON));
		filterButton_.setToolTipText("Start/Stop filtering display.");
		filterButton_.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(inkstoneModel_==null) {return;}
				if( ((Button)e.widget).getSelection() ) {
					if(inkstoneModel_.size()==0) {
						((Button)e.widget).setSelection(false);
						return;
					}
					filterButton_.setImage(inkGallery_.getImage(InkstoneGallery.GENERAL_CANCEL_ICON));
					filterTextbox_.setBackground(display_.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
				}
				else {
					filterButton_.setImage(inkGallery_.getImage(InkstoneGallery.GENERAL_SEARCH_ICON));
					filterTextbox_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
					filterTextbox_.setText(".*");
				}
				filterKioskBars(filterTextbox_.getText());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		FormData mainExpBarData = new FormData();
		mainExpBarData.top = (new FormAttachment(filterButton_, 2));
		mainExpBarData.bottom = (new FormAttachment(100,-2));
		mainExpBarData.left = (new FormAttachment(0, 0));
		mainExpBarData.right = (new FormAttachment(100, 0));
		mainExpBar_.setLayoutData(mainExpBarData);
		
		popupMenuListener_ = new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent e) {
				popupMenu_.setLocation(e.x, e.y);
				popupMenu_.setVisible(true);
				while (!popupMenu_.isDisposed () && popupMenu_.isVisible ()) {
					if (!display_.readAndDispatch ()) {
						display_.sleep ();
					}
				}
				popupMenu_.setVisible(false);
			}
		};
		mainExpBar_.addMenuDetectListener(popupMenuListener_);
	
		//addGeneralExpandBar(mainExpBar_);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	private void initPopupMenu() {
		
		MenuItem item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("Collapse All");
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				doCollapseAll();
			}
	    });
	    
	    item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("Expand All");
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				doExpandAll();
			}
	    });
	    
	    item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("Get Statistics");
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				String statisticsMsg;
				MessageBox msgBox = new MessageBox(KioskView.this.shell_, SWT.ICON_INFORMATION);
				msgBox.setText("InkStone Kiosk Statistics");
				statisticsMsg = "Total Elements: " + KioskView.this.getElementsCount() + "\n" +
				                "Total visiable elements: " + InkstoneElement.getNumOfVisualElements() + "\n";
				
				if( inkstoneModel_ != null ) {
					statisticsMsg += "Number of Ink projects loaded: " + inkstoneModel_.size() + "\n\n";
					for( InkstoneProject project : inkstoneModel_) {
						statisticsMsg += project.getStatistics();
					}
				}
				
				msgBox.setMessage(statisticsMsg);
				msgBox.open();
			}
	    });
	    
	    shell_.setMenu(popupMenu_);
	    
	}
	
	private void doCollapseAll() {
		setExpandedKioskBars(false);	
	}
	
	private void doExpandAll() {
		setExpandedKioskBars(true);		
	}
	
	/**
	 * Inner method for expand/collapse all expand bars in the INK Kiosk view.
	 * @param expanded TRUE for set all as expanded bars, FALSE for set as all as collapse bar.
	 */
	private void setExpandedKioskBars(boolean expanded) {
		int heightMark =(expanded ? 1 : (-1) );
		
		if( !hideInkNotations_ ) {
			if(mainExpBar_.getItem(0).getExpanded() != expanded ) {
				mainExpBar_.getItem(0).setExpanded(expanded);
			}
		}
		
		if(inkstoneModel_==null) {return;}
		
		for (InkstoneProject project: inkstoneModel_) {
			if( project.getExpandItem().getExpanded() != expanded ) {
				project.getExpandItem().setExpanded(expanded);
			}			
			for(InkstoneLibrary library : project.getDslLibs()) {
				if( library.getExpandItem().getExpanded() != expanded ) {
					project.setDisplayHeight(library.getDisplayHeight() * heightMark);
					library.getExpandItem().setExpanded(expanded);
				}				
				for(InkstoneElementKind kind : library.getinkTypes()) {
					if( kind.getExpandItem().getExpanded() != expanded ) {
						library.setDisplayHeight(kind.getDisplayHeight() * heightMark);
						kind.getExpandItem().setExpanded(expanded);
					}
				}				
			}			
		}
	}

	private void showHideInkNotations(boolean checked) {
		hideInkNotations_ = !checked;
		if(!hideInkNotations_) {
			addGeneralExpandBar(mainExpBar_);
		}
		else {
			mainExpBar_.getItem(0).getControl().dispose();
			mainExpBar_.getItem(0).dispose();
			mainExpBar_.redraw();
		}
	}
	
	/**
	 * Refresh the kiosk display using silent mode of the DSLsSelectionDialog.
	 */
	private void refreshModelExpandBars() {
		mainExpBar_.setVisible(false);
		
		// disable filtering display (refresh cancel last filter, if exist)
		filterButton_.setImage(inkGallery_.getImage(InkstoneGallery.GENERAL_SEARCH_ICON));
		filterButton_.setSelection(false);
		filterTextbox_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
		filterTextbox_.setText(".*");
		
		setExpandedKioskBars(true);
		//refresh all libraries objects in the inkstone model
		if( inkstoneModel_ != null ) {
			if(inkstoneModel_.size() > 0) {
				for (InkstoneProject project: inkstoneModel_) {
					for(InkstoneLibrary library : project.getDslLibs()) {
						try {
							library.refreshData();
						} catch (KioskOverloadOfVisualElementsException e) {
							KioskOverloadOfVisualElementsMessgae(e.getMessage());
						}
					}
				}
			}
		}
		if( selectedElement_ != null ) {
			selectedElement_.setSelected(true);
		}
		//refresh expand bars display by Collapse and Expand all bars
		setExpandedKioskBars(false);
		setExpandedKioskBars(true);
		
		mainExpBar_.setVisible(true);
	}
	
	private void openDSLsSelectionDialog() {
		// Update again preference limits
		maxAllowedElement_ = inkstone.Activator.getDefault().getPreferenceStore().getInt(InkstonePreferenceConstants.MAX_KIOSK_ALLOWED_ELEMENTS);
		maxViewedElement_ =	inkstone.Activator.getDefault().getPreferenceStore().getInt(InkstonePreferenceConstants.MAX_KIOSK_VISUAL_ELEMENTS);
		// Open the selection dialog
		switch (dslsSelectionDialog_.open()) {
			case DSLsSelectionDialog.DONE:
				clearModelExpandBars();
				drawInKiosk(dslsSelectionDialog_.getSelectedInkstoneModel());
				break;
			case DSLsSelectionDialog.CANCEL:
				// Do nothing !
				break;
			default:
				// Do nothing !
				break;
		}
	}	
	
	/**
	 * Clears expand bars from the kiosk view.
	 */
 	private void clearModelExpandBars() {
		if ( this.inkstoneModel_ != null ) {
			if ( this.inkstoneModel_.size() > 0 ) {
				BusyIndicator.showWhile(display_, new Runnable() {
					@Override
					public void run() {
						for (final InkstoneProject project: inkstoneModel_) {
	  						project.dispose();
	  					}
	  					inkstoneModel_.clear();
						for (int i = 1; i < mainExpBar_.getItems().length; i++) {
							mainExpBar_.getItem(i).dispose();
						}
						InkstoneElement.resetNumOfVisualElements();
					}
				}); 
			}
		}
	}

 	private int getElementsCount() {
 		
 		if( inkstoneModel_ == null ) { return 0; }
 		else if( inkstoneModel_.isEmpty()) { return 0; }
 		
 		int elementsSum = 0;
 		if( inkstoneModel_.size() > 0 ) {
	 		for( InkstoneProject project : inkstoneModel_) {
	 			elementsSum += project.getElementsCount();
			}
 		}
 		return elementsSum;
 	}
 	
	private void drawInKiosk(List<InkstoneProject> selectedInktoneModel) {
		this.inkstoneModel_= selectedInktoneModel;
		if( inkstoneModel_.size() == 0 ) return;
		
		Job job = new Job("Kiosk drawing ...") {
			@Override
			public IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Drawing INK Kiosk view ...", 100);
				for( final InkstoneProject project : inkstoneModel_) {
					if( InkstoneElement.getStopFlag() ) { break; }
		  			final int progressSteps = 100 / inkstoneModel_.size();
		  			display_.syncExec(new Runnable() {
		  				public void run() {
				  			try {
				  				project.drawInKiosk(mainExpBar_, popupMenuListener_, hideInkNotations_, monitor, progressSteps);
				  			} catch (KioskOverloadOfVisualElementsException e) {
								// Dispose the model and show over load error message:
								KioskOverloadOfVisualElementsMessgae(e.getMessage());
							}
		  				}
		  			});
		  			if( InkstoneElement.getStopFlag() ) {
		  				InkstoneElement.resetStopFlag();
		  				break;
		  			}
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		job.schedule();
		if( InkstoneElement.getStopFlag() ) { InkstoneElement.resetStopFlag(); }
	}
	
	private void addGeneralExpandBar(ExpandBar bar) {
		Composite composite = new Composite (bar, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		layout.marginWidth = layout.marginHeight = 0;
		composite.setLayout(layout);
		composite.addMenuDetectListener(popupMenuListener_);
		
		CLabel label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(InkstoneGallery.METACLASS_ICON));
		label.setText("INK MetaClass");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(InkstoneGallery.CLASS_ICON));
		label.setText("INK Class");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(InkstoneGallery.OBJECT_ICON));
		label.setText("INK Object");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(InkstoneGallery.ENUMERATION_ICON));
		label.setText("INK Enumeration");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		label = new CLabel(composite, SWT.SEPARATOR | SWT.CENTER);
		label.setText("  --------------------");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(InkstoneGallery.EXTENSION_ICON));
		label.setText("INK Extension");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(InkstoneGallery.INSTANCING_ICON));
		label.setText("INK Instancing");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(InkstoneGallery.REFERENCING_ICON));
		label.setText("INK Referencing");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));

		ExpandItem notationsPanel = new ExpandItem(bar, SWT.NONE, 0);
		notationsPanel.setText("INK Notations");
		notationsPanel.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		notationsPanel.setControl(composite);
		notationsPanel.setExpanded(true);
	}
	

	/**
	 * @return The current selected Inkstone element.
	 */
	public InkstoneElement getSelectedElement() {
		return this.selectedElement_;
	}
	
	/**
	 * Set the current selected Inkstone element.
	 * @param element An element to set as selecte in the Kiosk View.
	 */
	public void setSelectedElement(InkstoneElement element) {
		this.selectedElement_ = element;
	}

	/**
	 * Inner utility to lunch a filter display.
	 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#sum">Summary of regular-expression</a>

	 * @param regex - regular expression string. 
	 */
	private void filterKioskBars(final String regex) {
		mainExpBar_.setVisible(false);
		setExpandedKioskBars(true);	
		if ( this.inkstoneModel_ != null ) {
			if ( this.inkstoneModel_.size() > 0 ) {
				BusyIndicator.showWhile(display_, new Runnable() {
					@Override
					public void run() {
						for (InkstoneProject project: inkstoneModel_) {
							for (InkstoneLibrary library : project.getDslLibs()) {
								for (InkstoneElementKind kind : library.getinkTypes()) {
									for (int i = (kind.getCurrentPage()-1)*kind.getPageSize(); (i < kind.getElements().size()) && (i < kind.getCurrentPage()*kind.getPageSize()); i++) {
										kind.getElements().get(i).redrawByRegexMatch(regex);
										if( InkstoneElement.getStopFlag() ) { break; }
									}
								}
							}
						}
					}
				});
			}
		}
		mainExpBar_.setVisible(true);
		if( InkstoneElement.getStopFlag() ) { InkstoneElement.resetStopFlag(); }
	}
	
	private void KioskOverloadOfVisualElementsMessgae(String msg) {
		MessageBox messageBox = new MessageBox(this.shell_, SWT.ICON_ERROR);
		messageBox.setText("InkStone Kiosk error !");
		messageBox.setMessage(msg);
		messageBox.open();
	}
	
	@Override
 	public void dispose() {
		super.dispose();
		dslsSelectionDialog_.close();
		clearModelExpandBars();
	}
}