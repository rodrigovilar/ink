package inkstone.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import org.eclipse.swt.widgets.Display;

/**
 * A singleton class utility that stores InkStone plugin images.
 * InkStone images are stored in the 'utils/gallery' folder.
 *  
 * @author Ofer Calvo
 */
public class InkstoneGallery {

	private final static InkstoneGallery instance_ = new InkstoneGallery();
	private final Display display_ = Display.getDefault();
	private ImageRegistry registry_;
	
	public static final String      GENERAL_ADD_ICON = "General add icon";
	public static final String   GENERAL_DELETE_ICON = "General delete icon";
	public static final String        METACLASS_ICON = "metaclass icon";
	public static final String            CLASS_ICON = "class icon";
	public static final String           OBJECT_ICON = "object icon";
	public static final String      ENUMERATION_ICON = "enumerations icon";
	public static final String        EXTENSION_ICON = "extension icon";
	public static final String       INSTANCING_ICON = "instancing icon";
	public static final String            PAINT_ICON = "paint icon"; //paint_icon.png
	public static final String      REFERENCING_ICON = "referencing icon";
	public static final String  PROJECTEXP_PLUS_ICON = "projects Explorer plus icon";
	public static final String PROJECTEXP_MINUS_ICON = "projects Explorer minus icon";
	public static final String       INKPROJECT_ICON = "INK Projects icon";
	public static final String        INKDSLLIB_ICON = "INK DSL Lib icon";
	public static final String   GENERAL_SEARCH_ICON = "Search icon";
	public static final String   GENERAL_CANCEL_ICON = "Cancel icon";
	public static final String  GENERAL_REFRESH_ICON = "Refresh icon";
	public static final String     INKSTONE_BIG_LOGO = "INKSTONE Big Logo";
	public static final String     INKSTONE_MED_LOGO = "INKSTONE Medium Logo";
	public static final String   INKSTONE_SMALL_LOGO = "INKSTONE Small Logo";
	public static final String   INKSTONE_KIOSK_ICON = "INKSTONE KIOSK icon";
	public static final String  INKSTONE_DIAG_NORMAL = "INKSTONE Diagram Normal icon";
	public static final String  INKSTONE_DIAG_ERROR  = "INKSTONE Diagram Error icon";
	public static final String INKSTONE_PERSPTV_ICON = "INKSTONE Perspective icon";

	
	/**
	 * Class constructor. Builds the image registry.
	 */
	private InkstoneGallery() {
		registry_ = new ImageRegistry(Display.getCurrent()); 
		registry_.put(METACLASS_ICON        , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_meta_class_icon.png"));
		registry_.put(CLASS_ICON            , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_class_icon.png"));
		registry_.put(OBJECT_ICON           , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_object_icon.png"));
		registry_.put(ENUMERATION_ICON      , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_enum_icon.png"));
		registry_.put(EXTENSION_ICON        , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_extension_icon.png"));
		registry_.put(INSTANCING_ICON       , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_instancing_icon.png"));
		registry_.put(PAINT_ICON            , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\paint_icon.png"));
		registry_.put(REFERENCING_ICON      , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_referencing_icon.png"));
		registry_.put(GENERAL_ADD_ICON      , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\add_icon.png"));
		registry_.put(GENERAL_DELETE_ICON   , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\delete_icon.png"));
		registry_.put(INKPROJECT_ICON       , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_project.png"));
		registry_.put(INKDSLLIB_ICON        , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\ink_DSL_Lib.png"));
		registry_.put(GENERAL_SEARCH_ICON   , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\magnifying_glass_icon.gif"));
		registry_.put(GENERAL_CANCEL_ICON   , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\cancel_icon.gif"));
		registry_.put(GENERAL_REFRESH_ICON  , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\refresh_icon.png"));
		registry_.put(INKSTONE_BIG_LOGO     , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\inkstone_big_logo.jpg"));
		registry_.put(INKSTONE_MED_LOGO     , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\inkstone_medium_logo.jpg"));
		registry_.put(INKSTONE_SMALL_LOGO   , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\inkstone_small_logo.jpg"));
		registry_.put(INKSTONE_KIOSK_ICON   , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\INKSTONE Kiosk View Icon.png"));
		registry_.put(INKSTONE_DIAG_NORMAL  , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\INK Diagram Icon - Normal.png"));
		registry_.put(INKSTONE_DIAG_ERROR   , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\INK Diagram Icon - Error.png"));
		registry_.put(INKSTONE_PERSPTV_ICON , ImageDescriptor.createFromFile(InkstoneGallery.class, "\\gallery\\inkstone_perspective_icon.png"));
	};
	
	/**
	 * @return The inkstoneGallery single instance.
	 */
	public static InkstoneGallery getInstance() {
		return instance_; 
	}
	
	/**
	 * Get InkStone image from the image registry.
	 * 
	 * @param imageKey string key (use class final constants).
	 * @return The relevant Image object (Success). If missing returns the SWT.ICON_ERROR image (Fail).
	 */
	public Image getImage(String imageKey) {
		Image image = null;
		try {
			image = registry_.get(imageKey);
		} catch (Exception ex) {
			image = display_.getSystemImage(SWT.ICON_ERROR);
		}
		return image;
	}
	
	/**
	 * Get image descriptor object to get the image only by demand (economic image useage).
	 * 
	 * @param imageKey string key (use class final constants).
	 * @return The relevant Image descriptor object (Success). If missing returns NULL (Fail).
	 */
	public ImageDescriptor getImageDescriptor(String imageKey) {
		ImageDescriptor imgdes = null;
		try {
			imgdes = registry_.getDescriptor(imageKey);
		} catch (Exception ex) {
			imgdes = null;
		}
		return imgdes;
	}
	
	public void dispose() {
		registry_.dispose();
		instance_.dispose();
	}
	
}
