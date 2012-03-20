package org.ink.eclipse.editors.operations;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.eclipse.editors.page.DataBlock;
import org.ink.eclipse.editors.page.ObjectDataBlock;
import org.ink.eclipse.generators.BehaviorClassGenerator;
import org.ink.eclipse.generators.Generator;
import org.ink.eclipse.generators.InterfaceClassGenerator;
import org.ink.eclipse.utils.EclipseUtils;

public class GenerateJavaOperation extends InkEditorOperation {

	@Override
	protected boolean execute(InkObject o, Shell shell, IDocument doc, IFile file) throws Exception {
		if (o.reflect().isClass()) {
			ClassMirror cm = o.reflect();
			if (cm.isStruct()) {
				showDialog(shell, "The Ink class " + cm.getShortId() + " is a Struct and can't have Java Behavior/Interface classes.", MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL });
			} else {
				IPath interfacePath = EclipseUtils.getJavaInterfacePath(cm);
				IFile f = EclipseUtils.getJavaFile(file.getProject(), interfacePath);
				boolean hasInterface = f.exists();
				IPath behviorPath = EclipseUtils.getJavaBehaviorPath(cm);
				f = EclipseUtils.getJavaFile(file.getProject(), behviorPath);
				boolean hasBehavior = f.exists();
				JavaMapping existingJM = cm.getJavaMapping();
				JavaMapping jm = null;
				if (hasBehavior && hasInterface) {
					showDialog(shell, "Behavior and Interface classes already exist. Nothing to generate.", MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL });
				} else if (hasInterface) {
					int result = showDialog(shell, "Generate behavior class ?", MessageDialog.QUESTION, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL });
					if (result == 0) {
						jm = JavaMapping.Behavior_Interface;
						genrateBehaviorClass(cm, true, file);
					}
				} else if (hasBehavior) {
					int result = showDialog(shell, "Generate interface class ?", MessageDialog.QUESTION, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL });
					if (result == 0) {
						jm = JavaMapping.Behavior_Interface;
						genrateInterfaceClass(cm, file);
					}
				} else {
					int result = showDialog(shell, "Generate java classes ?", MessageDialog.QUESTION, new String[] { "Generate Behavior", "Generate Interface", "Generate Both", IDialogConstants.CANCEL_LABEL });
					if (result == 0) {
						jm = JavaMapping.Only_Behavior;
						genrateBehaviorClass(cm, false, file);
					} else if (result == 1) {
						jm = JavaMapping.Only_Interface;
						genrateInterfaceClass(cm, file);
					} else if (result == 2) {
						jm = JavaMapping.Behavior_Interface;
						genrateInterfaceClass(cm, file);
						genrateBehaviorClass(cm, true, file);
					}
				}
				if (jm != null) {
					jm = getTragetJavaMapping(jm, existingJM);
					if (jm != null) {
						ObjectDataBlock root = pa.getRootElement();
						List<DataBlock> blocks = root.getBlocks(CoreNotations.Properties.CLASS_JAVA_MAPPING);
						if (blocks != null && !blocks.isEmpty()) {
							DataBlock db = blocks.get(0);
							char[] text = db.getText();
							int offset = findOffset(db, '\"');
							int length = 0;
							int i = offset;
							for (; i < text.length; i++) {
								if (text[i] == '\"') {
									break;
								}
								length++;
							}
							doc.replace(offset, length, jm.toString());
							return true;
						} else {
							int offset = findOffset(root, '\n');
							doc.replace(offset, 0, "\t" + CoreNotations.Properties.CLASS_JAVA_MAPPING + " \"" + jm.toString() + "\"\n");
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private void genrateInterfaceClass(ClassMirror cm, IFile file) throws Exception {
		Generator gen = new InterfaceClassGenerator(computeOutputFolder(file));
		gen.generate(cm);
	}

	private void genrateBehaviorClass(ClassMirror cm, boolean hasInterface, IFile file) throws Exception {
		Generator gen = new BehaviorClassGenerator(computeOutputFolder(file), hasInterface);
		gen.generate(cm);
	}

	private IFolder computeOutputFolder(IFile file) throws Exception {
		return EclipseUtils.getJavaSourceFolder(file);
	}

	private int findOffset(DataBlock db, char delimiter) {
		int offset = db.getStartIndex();
		char[] text = db.getText();
		for (; offset < text.length; offset++) {
			if (text[offset] == delimiter) {
				offset++;
				break;
			}
		}
		return offset;
	}

	private JavaMapping getTragetJavaMapping(JavaMapping jm, JavaMapping existingJM) {
		if (jm.equals(existingJM)) {
			return null;
		}
		return jm.withState();
	}

	private int showDialog(Shell shell, String msg, int msgType, String[] buttons) {
		MessageDialog dialog = new MessageDialog(shell, "Generate Java Classes Dialog", null, msg, msgType, buttons, 0) {
			@Override
			protected int getShellStyle() {
				return super.getShellStyle() | SWT.SHEET;
			}
		};
		return dialog.open();
	}

	@Override
	protected boolean findTopLevelObjectOnly() {
		return true;
	}

}
