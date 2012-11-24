package inkstone.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


import inkstone.Activator;

public class InkstonePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String ID_ = "inkstone.preferences.InkstonePreferencePage";
	
	public InkstonePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("InkStone plug-in preference page settings :");
	}
	
	@Override
	/**
	 * The InkStone preference page field editors settings.
	 */
	protected void createFieldEditors() {
		
		CLabel spacer1 = new CLabel(getFieldEditorParent(), SWT.FILL_WINDING);
		CLabel spacer2 = new CLabel(getFieldEditorParent(), SWT.FILL_WINDING);
		Group kioskGroup = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
		kioskGroup.setText(" -= Kiosk View settings =- ");
		
		addField(new IntegerFieldEditor(InkstonePreferenceConstants.KIOSK_PAGE_SIZE,
					"Start using Kiosk view expand-bars inner pages when number of elements per expand-bar exceed this value :",
					kioskGroup));
		addField(new IntegerFieldEditor(InkstonePreferenceConstants.MAX_KIOSK_ALLOWED_ELEMENTS,
					"Maximum elements &allowed to be loded to InkStone Kiosk view :",
					kioskGroup));
		addField(new IntegerFieldEditor(InkstonePreferenceConstants.MAX_KIOSK_VISUAL_ELEMENTS,
				   "Maximum elements allowed to be &visiable at once in the InkStone Kiosk view :",
				   kioskGroup));
		CLabel note1 = new CLabel( kioskGroup, SWT.WRAP);
		note1.setText("  [!]  NOTE : In case of performance issues, use page size to reduce visiable elements shown at once.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
}
