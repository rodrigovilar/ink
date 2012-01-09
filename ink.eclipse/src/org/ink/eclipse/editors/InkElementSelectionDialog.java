package org.ink.eclipse.editors;

import java.util.Collection;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.mirror.Mirror;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.InkUtils;

public class InkElementSelectionDialog extends FilteredItemsSelectionDialog {

	private static final String DIALOG_SETTINGS = "org.ink.dialog.InkElementSelectionDialog";

	public InkElementSelectionDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = InkPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = InkPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}
		return settings;
	}

	@Override
	protected IStatus validateItem(Object item) {
		return new Status(IStatus.OK, InkPlugin.PLUGIN_ID, 0, "", null);
	}

	@Override
	protected ItemsFilter createFilter() {
		return new InkItemsFilter();
	}

	@Override
	protected Comparator<String> getItemsComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
		// TODO Auto-generated method stub
		Collection<Mirror> all = InkUtils.getInstances(CoreNotations.Ids.INK_OBJECT, true);
		for (Mirror m : all) {
			contentProvider.add(m.getShortId() + " - " + m.getNamespace(), itemsFilter);
		}
	}

	@Override
	public String getElementName(Object item) {
		// TODO Auto-generated method stub
		return null;
	}

	private final class InkItemsFilter extends ItemsFilter {

		@Override
		public boolean matchItem(Object item) {
			return super.matches(item.toString());
		}

		@Override
		public boolean isConsistentItem(Object item) {
			return true;
		}

	}

}
