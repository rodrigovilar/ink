package inkstone.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import org.ink.core.vm.mirror.Mirror;


/**
 * The 3rd level object type in the ink descriptive model (used by the InkStone plug-in).
 * Stores list of the same Ink Type/kind of ink elements (Metaclass/Class/Object/Enumeration).
 * Also stores widgets display data.
 * 
 * @author Ofer Calvo
 *
 */
public class InkstoneElementKind {
	private String kindName_;
	private InkstoneLibrary DslLib_;
	private List<InkstoneElement> elements_ = new ArrayList<InkstoneElement>();
	private Image image_;
	private ExpandItem expandItem_;
	private Composite childComposite_;
	private ExpandBar expandBar_;
	private int displayHeight_ = 0;
	
	/**
	 * Class constructor.
	 * 
	 * @param kindName The list name.
	 * @param DslLib Reference to the parent library.
	 * @param mirrors A list of reflected ink objects from same kind. 
	 */
	public InkstoneElementKind(String kindName, InkstoneLibrary DslLib, List<Mirror> mirrors, Image image) {
		this.kindName_ = kindName;
		this.DslLib_ = DslLib;
		this.image_ = image;
		for( Mirror m : mirrors) {
			elements_.add(new InkstoneElement(m.getShortId(), m.getNamespace(), this));
		}
	}
	
	public String getKindName() {
		return this.kindName_;
	}
	
	/**
	 * @return The kiosk expand bar item of this kind.
	 */
	public ExpandItem getExpandItem() {
		return this.expandItem_;
	}
	
	/**
	 * @return The kiosk expand bar of this kind.
	 */
	public ExpandBar getExpandBar() {
		return this.expandBar_ ;
	}
	
	public InkstoneLibrary getDslLib() {
		return this.DslLib_;
	}
	
	/**
	 * Draw the kiosk view display widgets.
	 * 
	 * @param parentComposite The ink kind parent composite widget.
	 */
	public void drawInKiosk(ExpandBar parentComposite) {
		this.expandBar_ = parentComposite;
		Display display = parentComposite.getDisplay();
		
		childComposite_ = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		layout.marginWidth = layout.marginHeight = 0;
		childComposite_.setLayout(layout);
		childComposite_.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		for (InkstoneElement element : elements_) {
			element.drawInKiosk(childComposite_, image_);
		}
		displayHeight_ = childComposite_.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		
		expandItem_ = new ExpandItem(parentComposite, SWT.NONE, 0);
		expandItem_.setHeight(displayHeight_);
		expandItem_.setText("    " + this.kindName_ + " (" + String.valueOf(elements_.size()) + " elements)");
		expandItem_.setControl(childComposite_);
	}

	/**
	 * @return The elements list.
	 */
	public List<InkstoneElement> getElements() {
		return elements_ ;
	}
	
	/**
	 * @return The height of this element.
	 */
	public int getDisplayHeight() {
		return this.displayHeight_;		
	}
	
	/**
	 * Set the widget display height.
	 * @param displayHeight The height.
	 */
	public void setDisplayHeight(int displayHeight) {
		if(displayHeight_== 4) {displayHeight_ = 0;}
		this.displayHeight_ += displayHeight;
		if(displayHeight_==0) {displayHeight_ = 4;}
		
		if(expandItem_.getExpanded()) {
			expandItem_.setHeight(displayHeight_);
			DslLib_.setDisplayHeight(displayHeight);
			expandBar_.redraw();
		}
	}
	
	public void refreshData(List<Mirror> mirrors) {
		int oldHeight = displayHeight_;
		int newHeight = 0;
		for (InkstoneElement element : elements_) {
			element.dispose();
		}
		elements_.clear();
		for( Mirror m : mirrors) {
			InkstoneElement element = new InkstoneElement(m.getShortId(), m.getNamespace(), this);
			elements_.add(element);
			element.drawInKiosk(childComposite_, image_);
		}
		newHeight = childComposite_.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		setDisplayHeight(newHeight-oldHeight);
	}
	
	public void dispose() {
		if(elements_ != null) {
			for(InkstoneElement element : elements_) {
				element.dispose();
			}
		}
		if( expandBar_ != null ) {
			expandBar_.dispose();
		}
		if( childComposite_ != null ) {
			childComposite_.dispose();
		}
	}
}
