package inkstone.models;

import inkstone.views.KioskView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.MenuDetectListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.core.resources.IProject;
import org.ink.eclipse.utils.InkUtils;

/**
 * A 1st level object type in the ink descriptive model (used by the InkStone plug-in).
 * Store INK Project display name, a list of its DSL ink libraries and display widgets.
 * 
 * @author Ofer Calvo
 */
public class InkstoneProject {

	private String name_;
	private List<InkstoneLibrary> DslLibs_ = new ArrayList<InkstoneLibrary>();
	private ExpandItem expandItem_;
	private ExpandBar expandBar_;
	private ExpandBar childComposite_;
	private int expandBarItemIndex_ = 1;
	private int displayHeight_ = 0;
	private KioskView parentKiosk_;
	private ExpandListener expandListener_;
		
	
	// 
	/**
	 * Class constructor.
	 * Auto starts by filling given INK project DSL namespaces.
	 * 
	 * @param p Eclipce resource project (must be an ink-project !).
	 */
	public InkstoneProject(IProject p, KioskView kiosk) {
		this.name_ = p.getName();
		this.parentKiosk_ = kiosk;
		String[] nss = InkUtils.getProjectNamespaces(p);
		for (int i = 0; i < nss.length; i++) {
			DslLibs_.add( new InkstoneLibrary(nss[i], this) );
		}
	}
	
	// Constructor for selective fill of the model
	/**
	 * Class constructor.
	 * Used for selective fill of the model when we want to hide part of the data in the InkStone descriptive model.
	 * 
	 * @param name The INK project name.
	 */
	public InkstoneProject(String name, KioskView kiosk) {
		this.name_ = name;
		this.parentKiosk_ = kiosk;
	}
	
	/**
	 * @return The project name.
	 */
	public String getName() {
		return this.name_;
	}
	
	/**
	 * @return The parent kiosk view.
	 */
	public KioskView getKiosk() {
		return this.parentKiosk_;
	}
	
	/**
	 * @return The DSL namespaces list.
	 */
	public List<InkstoneLibrary> getDslLibs() {
		return this.DslLibs_;
	}
	
	/**
	 * Add single DSL namespace library to the {@link InkstoneProject} list.
	 * Used for selective fill of the model when we want to hide part of the data in the InkStone descriptive model.
	 * @param DslLibName
	 */
	public void addDslLib(String DslLibName) {
		DslLibs_.add(new InkstoneLibrary(DslLibName, this));
	}
	
	/**
	 * @return The ExpandBar item bar holding this project display in the InkStone Kiosk view.
	 */
	public ExpandItem getExpandItem() {
		return this.expandItem_;
	}
	
	/**
	 * Set the ExpandBar item bar holding this project display in the InkStone Kiosk view.
	 * @param expandItem ExpandBar item bar ( {@link ExpandItem} )
	 */
	public void setExpandItem(ExpandItem expandItem) {
		this.expandItem_ = expandItem;
	}
	
	/**
	 * @return The {@link ExpandBar} object, parent of the item bar, holding this project display in the InkStone Kiosk view.
	 */
	public ExpandBar getExpandBar() {
		return expandBar_;
	}
	
	/**
	 * Set the ExpandBar object, parent of the item bar, holding this project display in the InkStone Kiosk view.
	 * @param expandBar {@link ExpandBar} object.
	 */
	public void setExpandBar(ExpandBar expandBar) {
		this.expandBar_ = expandBar;
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
		this.displayHeight_ += displayHeight;
		if(expandItem_.getExpanded()) {
			expandItem_.setHeight(displayHeight_);
			expandBar_.redraw();
		}
	}
	
	/**
	 * Draw the kiosk view display widgets.
	 * @param parentComposite The parent {@link Composite} object. Sub-type: {@link ExpandBar}.
	 */
	public void drawInKiosk(ExpandBar parentComposite, MenuDetectListener popupMenuListener ) {
		expandBar_ = parentComposite;
		
		childComposite_ = new ExpandBar (parentComposite, SWT.NONE);
		childComposite_.addMenuDetectListener(popupMenuListener);
		
		if( this.DslLibs_.size() > 0 ) {
			for (InkstoneLibrary library : this.DslLibs_) {
				library.drawInKiosk( childComposite_, popupMenuListener );
				displayHeight_ += library.getExpandItem().getHeaderHeight() + 4;
			}
		}
				
		expandItem_ = new ExpandItem(parentComposite, SWT.NONE, expandBarItemIndex_++);
		expandItem_.setHeight(displayHeight_);
		expandItem_.setText(this.name_ + " (" + String.valueOf(DslLibs_.size()) + " namespaces)");
		expandItem_.setControl(childComposite_);

		expandListener_ = new ExpandListener() {
			
			@Override
			public void itemExpanded(ExpandEvent e) {
				setDisplayHeight( ((ExpandItem)e.item).getHeight() );
			}
			
			@Override
			public void itemCollapsed(ExpandEvent e) {
				setDisplayHeight( -((ExpandItem)e.item).getHeight() );
			}
		};
		childComposite_.addExpandListener(expandListener_);
	}
	
	/**
	 * Clear sub widgets on disposing.
	 */
	public void dispose() {
//		if( (childComposite_ != null) && (expandListener_ != null) ) {
//			if( !childComposite_.getDisplay().isDisposed() ) {
//				childComposite_.removeExpandListener(expandListener_);
//			}
//		}
		if( this.DslLibs_ != null ) {
			for (InkstoneLibrary library : this.DslLibs_) {
				library.dispose();
			}
			DslLibs_.clear();
		}
		if( expandItem_ != null ) {
			expandItem_.dispose();
		}
		for (int i = 0; i < childComposite_.getItems().length; i++) {
			childComposite_.getItem(i).dispose();
		}
		childComposite_.dispose();
		expandBarItemIndex_= 1;
	}
}
