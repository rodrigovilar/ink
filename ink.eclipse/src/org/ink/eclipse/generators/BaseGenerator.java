package org.ink.eclipse.generators;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.EclipseUtils;
import org.ink.eclipse.utils.MD5Utils;

public abstract class BaseGenerator implements Generator {

	public final static String LINE_SEPARATOR = System.getProperty("line.separator");

	protected final IFolder outputFolder;

	public BaseGenerator(IFolder outputFolder) {
		boolean derived = true;
		if (sourceGenerator()) {
			derived = false;
			this.outputFolder = outputFolder;
		} else {
			this.outputFolder = outputFolder.getFolder("gen");
		}
		if (!this.outputFolder.exists()) {
			try {
				createFolder(outputFolder, derived);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	protected boolean sourceGenerator() {
		return false;
	}

	protected void writeFile(String data, String fullJavaPackage, String className, boolean isDerived) {
		try {
			String relativeFolderPath = fullJavaPackage.replace(".", File.separator);
			IFolder folder = outputFolder.getFolder(relativeFolderPath);
			createFolder(folder, isDerived);
			IFile f = folder.getFile(className + ".java");
			boolean shouldWrite = true;
			String newData = EclipseUtils.format(data);
			QualifiedName qn = new QualifiedName(InkPlugin.PLUGIN_ID, "hash");
			String newHash = MD5Utils.digest(newData);
			if (f.exists()) {
				String hash = f.getPersistentProperty(qn);
				if (hash != null && hash.equals(newHash)) {
					shouldWrite = false;
				} else {
					f.delete(true, null);
				}
			}
			if (shouldWrite) {
				byte[] bytes = newData.getBytes();
				f.create(new ByteArrayInputStream(bytes, 0, bytes.length), true, null);
				f.setPersistentProperty(qn, newHash);
				f.setDerived(isDerived, new NullProgressMonitor());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IContainer createFolder(IFolder folder, boolean derived) throws CoreException {
		if (!folder.exists()) {
			if (folder.getParent().getType() == IResource.FOLDER) {
				createFolder((IFolder) folder.getParent(), derived);
			}
			int flags = derived ? IResource.FORCE | IResource.DERIVED:IResource.FORCE;
			folder.create(flags, true, null);
		}
		return folder;
	}

	protected String convertPropertyName(String name) {
		StringBuilder result = new StringBuilder(name.length());
		result.append(Character.toUpperCase(name.charAt(0)));
		for (int i = 1; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '_') {
				if (i < name.length() - 1) {
					i++;
					result.append(Character.toUpperCase(name.charAt(i)));
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

}
