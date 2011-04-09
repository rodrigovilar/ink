package org.ink.eclipse.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.InkNotations;

public class EclipseUtils {

	@SuppressWarnings("unchecked")
	public static String format(String code) {
		// take default Eclipse formatting options
		Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

		// initialize the compiler settings to be able to format 1.5 code
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);

		// instantiate the default code formatter with the given options
		final CodeFormatter codeFormatter = ToolFactory
				.createCodeFormatter(options);

		// retrieve the source to format
		final TextEdit edit = codeFormatter.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0,
						System.getProperty("line.separator")); //$NON-NLS-1$
		if (edit == null) {
			new IllegalArgumentException("cannot format this: " + code).printStackTrace(); //$NON-NLS-1$
			return code;
		} else {
			// apply the format edit
			IDocument document = new Document(code);
			try {
				edit.apply(document);
			} catch (MalformedTreeException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return document.get();
		}
	}

	public static void openJava(InkObject o){
		Mirror m = o.reflect();
		if(!m.isClass()){
			m = m.getClassMirror();
		}
		ClassMirror cm = (ClassMirror)m;
		if(!cm.getJavaMapping().hasBeahvior()){
			cm = cm.getSuper();
		}
		String javaPath = cm.getFullJavaPackage();
		String javaFileName = cm.getShortId() + InkNotations.Names.BEHAVIOR_EXTENSION + ".java";
		IPath p = new Path(javaPath.replace(".", File.separator) + File.separatorChar + javaFileName);
		if (!o.reflect().isCoreObject()) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			File f = o.reflect().getDescriptor().getResource();
			IPath location = Path.fromOSString(f.getAbsolutePath());
			IFile file = workspace.getRoot().getFileForLocation(location);
			List<IClasspathEntry> paths = InkUtils.getJavaSrcPaths(file.getProject());
			IFile theFile = null;
			for(IClasspathEntry cpe : paths){
				IFolder folder = file.getProject().getFolder(cpe.getPath().removeFirstSegments(1));
				theFile = folder.getFile(p);
				if(theFile.exists()){
					break;
				}
			}
			if(theFile!=null){
				IEditorInput ei = new FileEditorInput(theFile);
				try {
					IJavaElement je = JavaUI.getEditorInputJavaElement(ei);
					JavaUI.openInEditor(je);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			FileEditorInput fei = (FileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			try{
				IJavaProject jProject = JavaCore.create(fei.getFile().getProject());
				IJavaElement je = jProject.findElement(p);
				JavaUI.openInEditor(je);
			}catch(Exception e){
			}
		}
	}


	public static void openEditor(InkObject o){
		if (!o.reflect().isCoreObject()) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			File f = o.reflect().getDescriptor().getResource();
			IPath location = Path.fromOSString(f.getAbsolutePath());
			IFile file = workspace.getRoot().getFileForLocation(location);
			IEditorInput ei = new FileEditorInput(file);
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().openEditor(ei, IDE.getEditorDescriptor(file.getName()).getId());
				revealInEditor(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage().getActiveEditor(),
						findOffset(f, "id=\"" + o.reflect().getShortId() + "\""),
						0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			FileEditorInput fei = (FileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			try{
				Mirror m = o.reflect();
				if(!m.isClass()){
					m = m.getClassMirror();
				}
				ClassMirror cm = (ClassMirror)m;
				if(!cm.getJavaMapping().hasState()){
					cm = cm.getSuper();
				}
				String javaPath = cm.getFullJavaPackage();
				String javaFileName = cm.getShortId() + InkNotations.Names.STATE_CLASS_EXTENSION + ".java";
				IPath p = new Path(javaPath.replace(".", File.separator) + File.separatorChar + javaFileName);
				IJavaProject jProject = JavaCore.create(fei.getFile().getProject());
				IJavaElement je = jProject.findElement(p);
				JavaUI.openInEditor(je);
			}catch(Exception e){
			}
		}

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
			gotoMarkerTarget = editor != null ? (IGotoMarker) editor
					.getAdapter(IGotoMarker.class) : null;
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
							marker = ((IFileEditorInput) input).getFile()
									.createMarker(IMarker.TEXT);
							marker.setAttribute(IMarker.CHAR_START, offset);
							marker.setAttribute(IMarker.CHAR_END, offset
									+ length);

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

	private static int findOffset(File f, String token) throws Exception {
		int offset = 0;
		boolean found = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(f), "UTF-8"));
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

}
