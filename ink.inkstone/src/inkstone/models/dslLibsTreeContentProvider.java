package inkstone.models;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ink.eclipse.InkPlugin;

import inkstone.utils.KioskOverloadOfVisualElements;
import inkstone.views.KioskView;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ITreeContentProvider} extension class for the {@link inkstone.dialogs.DSLsSelectionDialog} dialog class.
 * 
 * @author Ofer Calvo 
 */
public class dslLibsTreeContentProvider implements ITreeContentProvider {
	
	// Holds the tree root nodes
	private List<InkstoneProject> inktoneProjects_ = new ArrayList<InkstoneProject>();
	
	/**
	 * Class Constructor.
	 * Auto starts a build of a full ink descriptive model, by calling the {@link InkstoneProject} 
	 * constructor with {@link IProject} instances.
	 * @throws KioskOverloadOfVisualElements 
	 *  
	 *  @see inkstone.models.InkstoneProject
	 *  @see inkstone.models.InkstoneLibrary
	 *  @see inkstone.models.InkstoneElementKind
	 *  @see inkstone.models.InkstoneElement
	 */
	public dslLibsTreeContentProvider(KioskView kiosk) throws KioskOverloadOfVisualElements {
		List<IProject> inkProjects = InkPlugin.getDefault().getInkProjects();
		if(inkProjects!=null) {
			for (IProject p : inkProjects) {
				inktoneProjects_.add(new InkstoneProject(p, kiosk));
			}
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// INK model should not be changed from Tree view !
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// Get the root child nodes in the dslsSelectionDialog tree
		return inktoneProjects_.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// Only inkstoneProject has child nodes in the dslsSelectionDialog tree
		if( parentElement instanceof InkstoneProject ) {
			InkstoneProject project = (InkstoneProject)parentElement;
			return project.getDslLibs().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// Only the inkstoneLibrary has parent node in the dslsSelectionDialog tree 
		if( element instanceof InkstoneLibrary ) {
			return ((InkstoneLibrary)element).getProject();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// Only inkstoneProject has Child nodes in the dslsSelectionDialog tree
		if( element instanceof InkstoneProject ) {
			return true;
		}
		return false;
	}

}
