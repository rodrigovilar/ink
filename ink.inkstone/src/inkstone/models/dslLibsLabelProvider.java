package inkstone.models;

import inkstone.utils.InkstoneGallery;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A {@link LabelProvider} extension class for the {@link inkstone.dialogs.DSLsSelectionDialog} dialog class.
 * 
 * @author Ofer Calvo 
 */
public class dslLibsLabelProvider extends LabelProvider {
	
	// local variable for holding the InkStone gallery utility class
	private final InkstoneGallery inkGallery_;
	
	/**
	 * Class constructor.
	 */
	public dslLibsLabelProvider() {
		inkGallery_ = InkstoneGallery.getInstance();
	}
	
	@Override
	public String getText(Object element) {
		// Returns the ink-project (or the DSL namespace) name from the ink (descriptive) model
		if (element instanceof InkstoneProject) {
			InkstoneProject project = (InkstoneProject) element;
			return project.getName();
		}
		return ((InkstoneLibrary)element).getDslName();
	}
	
	@Override
	public Image getImage(Object element) {
		// Returns node image according to its type
		if (element instanceof InkstoneProject) {
			return inkGallery_.getImage(InkstoneGallery.INKPROJECT_ICON);
		}
		return inkGallery_.getImage(InkstoneGallery.INKDSLLIB_ICON);
	}

}