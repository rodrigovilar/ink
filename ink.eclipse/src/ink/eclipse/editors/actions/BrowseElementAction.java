package ink.eclipse.editors.actions;

import ink.eclipse.editors.InkElementSelectionDialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.utils.InkNotations;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.EclipseUtils;

public class BrowseElementAction implements IWorkbenchWindowActionDelegate,
		IActionDelegate2 {

	@Override
	public void run(IAction action) {
		SelectionDialog dialog = new InkElementSelectionDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.setTitle("Open Ink Element");
		// dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);

		int result = dialog.open();
		if (result != IDialogConstants.OK_ID) {
			return;
		}

		Object[] types = dialog.getResult();
		if (types == null || types.length == 0) {
			return;
		}
		String label = types[0].toString();
		int loc = label.indexOf("-");
		String fullId = label.substring(loc + 2, label.length())
				+ InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C
				+ label.substring(0, loc - 1);
		InkObject o = InkPlugin.getDefault().getInkContext().getFactory().getObject(fullId, false);
		if(o==null){
			EclipseUtils.openEditor(o);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void init(IAction action) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	@Override
	public void init(IWorkbenchWindow window) {
	}

}
