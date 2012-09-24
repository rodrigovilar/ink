package inkstone;

import inkstone.views.KioskView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * The Inkstone perspective.
 * Used to define the initial page layout when working with Inkstone.
 * 
 * @author Ofer Calvo
 */
public class InkstoneMainPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {

		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Project Explorer view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);

		// Bottom right: Problems view and the Task List view
		IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f, editorArea);
		bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottomRight.addView(IPageLayout.ID_TASK_LIST);
		
		// Right: Task Kiosk view
		layout.addView(KioskView.ID_, IPageLayout.RIGHT , 0.70f, editorArea);
	}

}
