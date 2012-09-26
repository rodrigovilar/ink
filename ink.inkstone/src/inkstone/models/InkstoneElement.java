package inkstone.models;

import inkstone.views.KioskView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.widgets.Shell;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.EclipseUtils;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.InkNotations;


/**
 * The 4th level object type in the ink descriptive model (used by the InkStone plug-in).
 * Stores display names and other useful data about ink-model elements (Metaclass/Class/Object/Enumeration).
 * Also stores widgets display data.
 * 
 * @author Ofer Calvo
 *
 */
public class InkstoneElement {

	private String name_;
	private String namespace_;
	private InkstoneElementKind elementkind_;
	private Composite composite_;
	private Display display_;
	private Shell shell_;
	private CLabel label_;
	private boolean selected_;
	private Menu popupMenu_;
	private MenuDetectListener popupMenuListener_;
	
	/**
	 * Class constructor.
	 * 
	 * @param name is the INK element short name.
	 * @param namespace is the INK namespace name.
	 */
 	public InkstoneElement(String name, String namespace, InkstoneElementKind elementkind) {
		this.name_ = name;
		this.namespace_ = namespace;
		this.elementkind_ = elementkind;
		this.selected_ = false;
	}
	
 	private void initPopupMenu() {
 		if((shell_ == null) || (composite_ == null)) return;
 		
 		popupMenu_ = new Menu(shell_, SWT.POP_UP);
 		MenuItem item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("&Open element");
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				openInkElement(getFullId());
			}
	    });
	    shell_.setMenu(popupMenu_);
 	}
 	
 	
	/**
	 * @return The element short name.
	 */
	public String getName() {
		return this.name_;
	}
	
	/**
	 * @return The element INK namespace name.
	 */
	public String getNamespace() {
		return this.namespace_;
	}
	
	/**
	 * @return The INK element kind parent {@link InkstoneElementKind} reference.
	 */
	public InkstoneElementKind getelementkind() {
		return this.elementkind_;
	}
	
	/**
	 * @return The element full name ( e.g. - 'INK_NAMESPACE:ELEMENT_NAME' ).
	 */
	public String getFullId() {
		return this.namespace_ + InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C + this.name_;
	}
	
	/**
	 * @return The interface of the base Ink behavior class described by this {@link InkstoneElement} object.
	 * 
	 * @see org.ink.core.vm.lang.InkObject
	 */
	public InkObject toInkObject() {
		return InkPlugin.getDefault().getInkContext().getFactory().getObject(getFullId(), false);
	}
	
	private Mirror getInkElement(String id){
		InkObjectState inkState = InkPlugin.getDefault().getInkContext().getState(id, false);
		if(inkState != null) {
			return inkState.reflect();
		}
		return null;
	}
	
	private void openInkElement(String id){
		Mirror mirror = getInkElement(id);
		if(mirror != null){
			EclipseUtils.openEditor(mirror);
			label_.setBackground(display_.getSystemColor(SWT.COLOR_LIST_SELECTION));
			setSelected(true, InkstoneElement.this);
		}
	}
	
	/**
	 * @return The {@link Composite} object, parent of the {@link CLable} label, holding this element display in the InkStone Kiosk view.
	 */
	public Composite getComposite() {
		return this.composite_;
	}
	
	/**
	 * @return The {@link CLable} label widget, holding this element display in the InkStone Kiosk view.
	 */
	public CLabel getLabel() {
		return this.label_;
	}
	
	/**
	 * @return TRUE if the label is currently selected.
	 */
	public boolean getSelected() {
		return  this.selected_;
	}
	
	private void setSelected(boolean selected, InkstoneElement callingElement) {
		this.selected_ = selected;
		if(elementkind_== null) { return; }
		KioskView kiosk = elementkind_.getDslLib().getProject().getKiosk();
		if(kiosk == null) { return; }
		
		if( selected ) {
			if(kiosk.getSelectedElement() != null) {
				kiosk.getSelectedElement().setSelected(false, this);
			}
			kiosk.setSelectedElement(this);
			//label_.setBackground(display_.getSystemColor(SWT.COLOR_LIST_SELECTION));
		}
		else {
			if(callingElement == this) {
				kiosk.setSelectedElement(null);
			}
			else {
				if(!label_.isDisposed()) {
					label_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
				}
			}
			//label_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
		}		
	}
	
	public void setSelected(boolean selected) {
		this.selected_ = selected;
		if( selected ) {
			label_.setBackground(display_.getSystemColor(SWT.COLOR_LIST_SELECTION));
		}
		else {
			label_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
		}
	}
	
	/**
	 * Set the ExpandBar object, parent of the item bar, holding this project display in the InkStone Kiosk view.
	 * @param expandBar {@link ExpandBar} object.
	 */
	public void setComposite(Composite composite) {
		this.composite_ = composite;
	}
	
	public void drawInKiosk(Composite composite, Image image ) {
		this.composite_ = composite;
		this.display_ = composite_.getDisplay();
		shell_ = new Shell(display_);
		initPopupMenu();
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
	
		label_ = new CLabel(composite, SWT.NONE);
		label_.setText( getFullId() );
		label_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
		label_.setImage(image);
		label_.setToolTipText(this.elementkind_.getDslLib().getProject().getName() + " - " + getName());
		label_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true,false));
		label_.addMenuDetectListener(popupMenuListener_);
		label_.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseDown(MouseEvent e) {				
				if(selected_) {
					label_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
					setSelected(false, InkstoneElement.this);
				}
				else {
					label_.setBackground(display_.getSystemColor(SWT.COLOR_LIST_SELECTION));
					setSelected(true, InkstoneElement.this);
				}
				label_.redraw();
				
			}
		});
		
		// Check if current element was selected before (in case of refreshing data in the kiosk)
		InkstoneElement lastKioskSelectedElement = elementkind_.getDslLib().getProject().getKiosk().getSelectedElement();
		if( lastKioskSelectedElement != null ) {
			if(lastKioskSelectedElement.getFullId().compareTo(getFullId()) == 0) {
				label_.setBackground(display_.getSystemColor(SWT.COLOR_LIST_SELECTION));
				setSelected(true, InkstoneElement.this);
			}
		}		
	}
	
	/**
	 * @return The height of this element.
	 */
	public int getDisplayHeight() {
		return label_.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
	}
	
	/**
	 * Redraws the element according to a name match to a given regular-expression string.
	 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#sum">Summary of regular-expression</a>
	 * @param regex  - the regular expression to which this element is to be matched. 
	 */
	public void redrawByRegexMatch(String regex) {
		GridData data = (GridData) label_.getLayoutData();
		boolean isaMatch = label_.getText().matches(regex);
		if(label_.getVisible()) {
			if(!isaMatch) {
	            data.exclude = true;
	            label_.setVisible(false);
	            composite_.setBackground(display_.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
	            composite_.layout(false);
	            elementkind_.setDisplayHeight(-getDisplayHeight());
			}
		}
		else {
			if(isaMatch) {
	            data.exclude = false;
	            label_.setVisible(true);
	            composite_.setBackground(display_.getSystemColor(SWT.COLOR_WHITE));
	            composite_.layout(false);
	            elementkind_.setDisplayHeight(getDisplayHeight());
			}
		}
	}
	
	/**
	 * Clear sub widgets on disposing.
	 */
 	public void dispose() {
		if( label_ != null ) {
			label_.dispose();
		}
	}

}
