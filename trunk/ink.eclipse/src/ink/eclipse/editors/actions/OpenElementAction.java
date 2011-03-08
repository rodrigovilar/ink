package ink.eclipse.editors.actions;

import ink.eclipse.editors.InkElementSelectionDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.utils.InkNotations;
import org.ink.eclipse.InkPlugin;

public class OpenElementAction implements IWorkbenchWindowActionDelegate,
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
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String label = types[0].toString();
			int loc = label.indexOf("-");
			String fullId = label.substring(loc + 2, label.length())
					+ InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C
					+ label.substring(0, loc - 1);
			InkObject o = InkPlugin.getDefault().getInkContext()
					.getObject(fullId);
			if (o != null && !o.reflect().isCoreObject()) {
				File f = o.reflect().getDescriptor().getResource();
				IPath location = Path.fromOSString(f.getAbsolutePath());
				IFile file = workspace.getRoot().getFileForLocation(location);
				IEditorInput ei = new FileEditorInput(file);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().openEditor(ei, InkPlugin.EDITOR_ID);
				try {
					revealInEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(),
							findOffset(f, "id=\"" + o.reflect().getShortId() + "\""), 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (CoreException x) {
			throw new RuntimeException(x);
		}
		return;
	}

	private int findOffset(File f, String token) throws Exception {
		int offset = 0;
		boolean found = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(token)) {
					found = true;
					break;
				}
				offset += line.length() + 2;
			}
		} finally {
			reader.close();
		}
		if (found) {
			return offset;
		}
		return 0;
	}

	public static void revealInEditor(IEditorPart editor, final int offset,
			final int length) {
		if (editor instanceof ITextEditor) {
			((ITextEditor) editor).selectAndReveal(offset, length);
			return;
		}

		// Support for non-text editor - try IGotoMarker interface
		final IGotoMarker gotoMarkerTarget;
		if (editor instanceof IGotoMarker) {
			gotoMarkerTarget = (IGotoMarker) editor;
		} else {
			gotoMarkerTarget = editor != null ? (IGotoMarker) editor.getAdapter(IGotoMarker.class) : null;
		}
		if (gotoMarkerTarget != null) {
			final IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
					@Override
					protected void execute(IProgressMonitor monitor)
							throws CoreException {
						IMarker marker = null;
						try {
							marker = ((IFileEditorInput) input).getFile().createMarker(IMarker.TEXT);
							marker.setAttribute(IMarker.CHAR_START, offset);
							marker.setAttribute(IMarker.CHAR_END, offset + length);

							gotoMarkerTarget.gotoMarker(marker);

						} finally {
							if (marker != null) {
								marker.delete();
							}
						}
					}
				};

				try {
					op.run(null);
				} catch (InvocationTargetException ex) {
				} catch (InterruptedException e) {

				}
			}
			return;
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
