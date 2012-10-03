package inkstone.models;

import inkstone.utils.InkstoneGallery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;
import org.ink.core.vm.mirror.Mirror;
import org.ink.eclipse.utils.InkUtils;

/**
 * A 2nd level object type in the ink descriptive model (used by the InkStone plug-in).
 * Stores DSL namespace display name and lists of its ink elements, by types.
 * Also store a reference to its parent INK-project and display widgets data.
 * 
 * @author Ofer Calvo
 */
public class InkstoneLibrary {

	private static final String ENUMERATIONS = "Enumerations";
	private static final String OBJECTS = "Objects";
	private static final String CLASSES = "Classes";
	private static final String META_CLASSES = "Meta-Classes";
	
	private String DslName_;
	private InkstoneProject project_;
	private List<InkstoneElementKind> inkTypes_ = new ArrayList<InkstoneElementKind>();
	private Map<String,InkstoneElementKind> inkTypesMap_ = new HashMap<String,InkstoneElementKind>();
	private ExpandItem expandItem_;
	private ExpandBar expandBar_;
	private ExpandBar childComposite_;
	private ExpandListener expandListener_;	
	private final InkstoneGallery inkGallery_;
	private int expandBarItemIndex_ = 0;
	private int displayHeight_ = 0;
	
	/**
	 * Class Constructor.
	 * Auto starts a build of the namespace ink elements, by their types.
	 * 
	 * @param DslName is the name of the ink namespace.
	 * @param project is a reference to the parent project object in the model.
	 */
	public InkstoneLibrary (String DslName, InkstoneProject project) {
	
		this.DslName_ = DslName;
		this.project_ = project;
		this.inkGallery_ = InkstoneGallery.getInstance();

		refreshData();
	}
	
	/**
	 * @return The library INK namespace.
	 */
	public String getDslName() {
		return this.DslName_;
	}
	
	/**
	 * @return The parent INK project of the INK namespace.
	 */
	public InkstoneProject getProject() {
		return this.project_;
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
	
	public List<InkstoneElementKind> getinkTypes() {
		return this.inkTypes_;
	}
	
	/**
	 * Set the widget display height.
	 * @param displayHeight The height.
	 */
	public void setDisplayHeight(int displayHeight) {
		this.displayHeight_ += displayHeight;
		if(expandItem_.getExpanded()) {
			expandItem_.setHeight(displayHeight_);
			project_.setDisplayHeight(displayHeight);
			expandBar_.redraw();
		}
	}
	
	/**
	 * Draw the kiosk view display widgets.
	 * @param parentComposite The parent {@link Composite} object. Sub-type: {@link ExpandBar}.
	 */
	public void drawInKiosk(ExpandBar parentComposite, MenuDetectListener popupMenuListener ) {
		expandBar_ = parentComposite;
		
		childComposite_ = new ExpandBar(parentComposite, SWT.NONE);
		childComposite_.addMenuDetectListener(popupMenuListener);
		
		if( inkTypes_.size() > 0 ) {
			for(InkstoneElementKind kind : inkTypes_) {
				kind.drawInKiosk( childComposite_ );
				displayHeight_ += kind.getExpandItem().getHeaderHeight() + 4;
			}
		}
		
		expandItem_ = new ExpandItem(parentComposite, SWT.NONE, expandBarItemIndex_++);
		expandItem_.setHeight(displayHeight_);
		expandItem_.setText( getBarText() );
		childComposite_.setToolTipText( getBarToolTip() );
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

	public String getBarText() {
		String text = DslName_;
		int elementsSum = 0;
		if( inkTypes_.size() > 0 ) {
			for(InkstoneElementKind kind : inkTypes_) {
				elementsSum += kind.getElements().size();
			}
			text += " (" + String.valueOf(elementsSum) + " elements)";
		}
		return text;
	}
	
	public String getBarToolTip() {
		String mapKeys[] = {META_CLASSES, CLASSES, OBJECTS, ENUMERATIONS};
		String text = DslName_ + " : \n";
		if( inkTypesMap_.size() > 0 ) {
			for( String key : mapKeys ) {
				if( inkTypesMap_.containsKey(key) ) {
					text += String.valueOf(inkTypesMap_.get(key).getElements().size()) + " " + inkTypesMap_.get(key).getKindName() + "\n";
				}
			}
					}
		return text;
	}
	
	/**
	 * Refresh the element-library data (or set new data at first constructor call).
	 */
	public void refreshData() {
		List<Mirror> inkMetaclasses 	= new ArrayList<Mirror>();
		List<Mirror> inkClasses 		= new ArrayList<Mirror>();
		List<Mirror> inkObjects 		= new ArrayList<Mirror>();
		List<Mirror> inkEnumerations 	= new ArrayList<Mirror>();
		
		InkstoneElementKind kind;
		String[] nss = {DslName_};
		Collection<Mirror> mirrors = InkUtils.getInstances(nss, "ink.core:InkObject", true, true);
		
		for(Mirror m : mirrors) {
			switch (m.getObjectTypeMarker()) {
				case Metaclass:
					inkMetaclasses.add(m);
					break;
				case Class:
						inkClasses.add(m);
					break;
				case Object:
					inkObjects.add(m);
					break;
				case Enumeration:
					inkEnumerations.add(m);
					break;
				default:
					// None supported types are ignored !
					break;
			}
		}

		if( inkEnumerations.size() > 0 ) {
			if(inkTypesMap_.containsKey(ENUMERATIONS)) {
				inkTypesMap_.get(ENUMERATIONS).refreshData(inkEnumerations);
			}
			else {
				kind = new InkstoneElementKind(ENUMERATIONS, this, inkEnumerations,
						inkGallery_.getImage(InkstoneGallery.ENUMERATION_ICON));
				inkTypes_.add(kind);
				inkTypesMap_.put(ENUMERATIONS, kind);
			}			
		}
		else {
			if(inkTypesMap_.containsKey(ENUMERATIONS)) {
				inkTypesMap_.get(ENUMERATIONS).dispose();
			}
		}
		
		if( inkObjects.size() > 0 ) {
			if(inkTypesMap_.containsKey(OBJECTS)) {
				inkTypesMap_.get(OBJECTS).refreshData(inkObjects);
			}
			else {
				kind = new InkstoneElementKind(OBJECTS, this, inkObjects,
						inkGallery_.getImage(InkstoneGallery.OBJECT_ICON));
				inkTypes_.add(kind);
				inkTypesMap_.put(OBJECTS, kind);
			}				
		}
		else {
			if(inkTypesMap_.containsKey(OBJECTS)) {
				inkTypesMap_.get(OBJECTS).dispose();
			}
		}
		
		if( inkClasses.size() > 0 ) {
			if(inkTypesMap_.containsKey(CLASSES)) {
				inkTypesMap_.get(CLASSES).refreshData(inkClasses);
			}
			else {
				kind = new InkstoneElementKind(CLASSES, this, inkClasses,
						inkGallery_.getImage(InkstoneGallery.CLASS_ICON));
				inkTypes_.add(kind);
				inkTypesMap_.put(CLASSES, kind);
			}
		}
		else {
			if(inkTypesMap_.containsKey(CLASSES)) {
				inkTypesMap_.get(CLASSES).dispose();
			}
		}
		
		if( inkMetaclasses.size() > 0 ) {
			if(inkTypesMap_.containsKey(META_CLASSES)) {
				inkTypesMap_.get(META_CLASSES).refreshData(inkMetaclasses);
			}
			else {
				kind = new InkstoneElementKind(META_CLASSES, this, inkMetaclasses,
						inkGallery_.getImage(InkstoneGallery.METACLASS_ICON));
				inkTypes_.add(kind);
				inkTypesMap_.put(META_CLASSES, kind);
			}
		}
		else {
			if(inkTypesMap_.containsKey(META_CLASSES)) {
				inkTypesMap_.get(META_CLASSES).dispose();
			}
		}
		
	}
	
	
	/**
	 * Clear sub widgets on disposing.
	 */
	public void dispose() {
		if( inkTypes_ != null ) {
			for (InkstoneElementKind kind : inkTypes_) {
				kind.dispose();
			}
			inkTypes_.clear();
		}
		if( expandItem_ != null ) {
			expandItem_.dispose();
		}
		if(!childComposite_.isDisposed()) {
			for (int i = 0; i < childComposite_.getItems().length; i++) {
				childComposite_.getItem(i).dispose();
			}
		}
		expandBarItemIndex_ = 0;
	}
}
