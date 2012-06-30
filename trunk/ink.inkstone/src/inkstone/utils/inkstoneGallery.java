package inkstone.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class inkstoneGallery {

	private final static inkstoneGallery instance_ = new inkstoneGallery();
	private final Display display_ = Display.getDefault();
	private ImageRegistry registry_;
	
	public static final String   METACLASS_ICON = "metaclass icon";
	public static final String       CLASS_ICON = "class icon";
	public static final String      OBJECT_ICON = "object icon";
	public static final String   EXTENSION_ICON = "extension icon";
	public static final String  INSTANCING_ICON = "instancing icon";
	public static final String REFERENCING_ICON = "referencing icon";
	
	private inkstoneGallery() {
		registry_ = new ImageRegistry(Display.getCurrent());
		registry_.put(METACLASS_ICON   , ImageDescriptor.createFromFile(inkstoneGallery.class, "\\gallery\\ink_meta_class_icon.png"));
		registry_.put(CLASS_ICON       , ImageDescriptor.createFromFile(inkstoneGallery.class, "\\gallery\\ink_class_icon.png"));
		registry_.put(OBJECT_ICON      , ImageDescriptor.createFromFile(inkstoneGallery.class, "\\gallery\\ink_object_icon.png"));
		registry_.put(EXTENSION_ICON   , ImageDescriptor.createFromFile(inkstoneGallery.class, "\\gallery\\ink_extension_icon.png"));
		registry_.put(INSTANCING_ICON  , ImageDescriptor.createFromFile(inkstoneGallery.class, "\\gallery\\ink_instancing_icon.png"));
		registry_.put(REFERENCING_ICON , ImageDescriptor.createFromFile(inkstoneGallery.class, "\\gallery\\ink_referencing_icon.png"));
	};
	
	public static inkstoneGallery getInstance() {
		return instance_; 
	}
	
	public Image getImage(String imageKey) {
		Image image = null;
		try {
			image = registry_.get(imageKey);
		} catch (Exception ex) {
			image = display_.getSystemImage(SWT.ICON_ERROR);
		}
		return image;
	}
	
}
