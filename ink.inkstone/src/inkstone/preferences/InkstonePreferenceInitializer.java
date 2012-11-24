package inkstone.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import inkstone.Activator;

public class InkstonePreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(InkstonePreferenceConstants.KIOSK_PAGE_SIZE, 100);
		store.setDefault(InkstonePreferenceConstants.MAX_KIOSK_ALLOWED_ELEMENTS, 5000);
		store.setDefault(InkstonePreferenceConstants.MAX_KIOSK_VISUAL_ELEMENTS, 1000);
	}

}
