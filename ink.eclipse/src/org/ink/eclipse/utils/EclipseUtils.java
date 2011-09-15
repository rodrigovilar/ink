package org.ink.eclipse.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.InkNotations;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.jobs.ErrorMessageJob;

public class EclipseUtils {


	public static IFile getEclipseFile(File f){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(f.getAbsolutePath());
		IFile file = workspace.getRoot().getFileForLocation(
				location);
		return file;
	}

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

	public static IJavaElement getJavaElement(ClassMirror cm, IPath p, boolean isCoreObject){
		IProject project = null;
		if (!isCoreObject) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			File f = cm.getDescriptor().getResource();
			IPath location = Path.fromOSString(f.getAbsolutePath());
			IFile file = workspace.getRoot().getFileForLocation(location);
			project = file.getProject();
		}else{
			project = InkPlugin.getDefault().getInkProjects().get(0);
		}
		IJavaProject jProject = JavaCore.create(project);
		IJavaElement je;
		try {
			je = jProject.findElement(p);
			return je;
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	public static IFile getJavaInterfaceFile(ClassMirror cm){
		if(!cm.getJavaMapping().hasInterface() || cm.isCoreObject()){
			return null;
		}
		return getJavaFile(cm, getJavaInterfacePath(cm));
	}

	public static IFile getJavaBehaviorFile(ClassMirror cm){
		if(!cm.getJavaMapping().hasBehavior() || cm.isCoreObject()){
			return null;
		}
		return getJavaFile(cm, getJavaBehaviorPath(cm));
	}

	public static IFolder getJavaSourceFolder(IFile inkFile) throws Exception{
		IFolder result = null;
		IProject project = inkFile.getProject();
		IJavaProject jProject = JavaCore.create(project);
		IClasspathEntry[] entries = jProject.getRawClasspath();
		boolean isMain = inkFile.getFullPath().toOSString().contains(File.separator + "main" +File.separator+"dsl" );
		for(IClasspathEntry en : entries){
			if(en.getEntryKind()==IClasspathEntry.CPE_SOURCE){
				if(isMain && en.getPath().toOSString().contains("main" +File.separator+"java")){
					return project.getFolder(en.getPath().removeFirstSegments(1));
				}else if(!isMain && en.getPath().toOSString().contains("test" +File.separator+"java")){
					return project.getFolder(en.getPath().removeFirstSegments(1));
				}
			}
		}
		return result;
	}

	private static IFile getJavaFile(ClassMirror cm, IPath p) {
		try{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			File f = cm.getDescriptor().getResource();
			IPath location = Path.fromOSString(f.getAbsolutePath());
			IFile file = workspace.getRoot().getFileForLocation(location);
			IProject project = file.getProject();
			IJavaProject jProject = JavaCore.create(project);
			IClasspathEntry[] entries = jProject.getRawClasspath();
			IFile result = null;
			for(IClasspathEntry en : entries){
				if(en.getEntryKind()==IClasspathEntry.CPE_SOURCE){
					IPath fullPath = en.getPath().append(p);
					fullPath = fullPath.makeRelativeTo(project.getFullPath());
					result = project.getFile(fullPath);
					if(result!=null){
						return result;
					}
				}
			}
		}catch(Exception e){
			e.fillInStackTrace();
		}
		return null;
	}

	public static IFile getJavaFile(IProject project, IPath p) {
		try{
			IJavaProject jProject = JavaCore.create(project);
			IClasspathEntry[] entries = jProject.getRawClasspath();
			IFile result = null;
			for(IClasspathEntry en : entries){
				if(en.getEntryKind()==IClasspathEntry.CPE_SOURCE){
					IPath fullPath = en.getPath().append(p);
					fullPath = fullPath.makeRelativeTo(project.getFullPath());
					result = project.getFile(fullPath);
					if(result!=null){
						return result;
					}
				}
			}
		}catch(Exception e){
			e.fillInStackTrace();
		}
		return null;
	}



	public static IJavaElement getJavaBehaviorElement(ClassMirror cm){
		while(!cm.getJavaMapping().hasBehavior() && cm.getSuper()!=null){
			cm = cm.getSuper();
		}
		IPath p = getJavaBehaviorPath(cm);
		return getJavaElement(cm, p, cm.isCoreObject());
	}

	public static IPath getJavaBehaviorPath(ClassMirror cm) {
		String javaPath = cm.getFullJavaPackage();
		String javaFileName = cm.getShortId() + InkNotations.Names.BEHAVIOR_EXTENSION;
		String className = javaPath + "." + javaFileName;
		IPath p = new Path(className.replace(".", File.separator) + ".java");
		return p;
	}

	public static IPath getJavaInterfacePath(ClassMirror cm) {
		String javaPath = cm.getFullJavaPackage();
		String javaFileName = cm.getShortId();
		String className = javaPath + "." + javaFileName;
		IPath p = new Path(className.replace(".", File.separator) + ".java");
		return p;
	}

	public static IJavaElement getJavaInterfaceElement(ClassMirror cm){
		while(!cm.getJavaMapping().hasInterface() && cm.getSuper()!=null){
			cm = cm.getSuper();
		}
		String javaPath = cm.getFullJavaPackage();
		String javaFileName = cm.getShortId();
		String className = javaPath + "." + javaFileName;
		IPath p = new Path(className.replace(".", File.separator) + ".java");
		return getJavaElement(cm, p, cm.isCoreObject());
	}

	public static void openJava(InkObject o){
		Mirror m = o.reflect();
		if(!m.isClass()){
			m = m.getClassMirror();
		}
		try{
			IJavaElement je = getJavaBehaviorElement((ClassMirror)m);
			openJavaElement(je);
		}catch(Exception e){
		}
	}

	public static IFolder getJavaOutputFolder(IProject p){
		try {
			IJavaProject jProject = JavaCore.create(p);
			IPath outputPath = jProject.getOutputLocation().removeFirstSegments(1);
			return p.getFolder(outputPath);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static IFile getOutputFile(IProject p, IFile sourceFile){
		try {
			IPath relativeFilepath = sourceFile.getFullPath().removeFirstSegments(4);
			if(relativeFilepath.isEmpty()){
				relativeFilepath = sourceFile.getFullPath().removeFirstSegments(1);
			}
			IFolder outputFolder = getJavaOutputFolder(p);
			IFile result = outputFolder.getFile(relativeFilepath);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IPath getJavaCompiledClass(IProject project, IFile javaSourceFile){
		try {
			IFile result = getOutputFile(project, javaSourceFile);
			IPath p = result.getFullPath().removeFileExtension().addFileExtension("class");
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void openJavaElement(IJavaElement je)
			throws JavaModelException, PartInitException {
		if(je.exists()){
			JavaUI.openInEditor(je);
		}else{
			Job errorJ = new ErrorMessageJob("Could not find the class '" +je.getElementName() +"'.");
			errorJ.setPriority(Job.INTERACTIVE);
			errorJ.schedule();
		}
	}

	public static IFile getFile(File f){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(f.getAbsolutePath());
		return workspace.getRoot().getFileForLocation(location);
	}

	public static int findLineNumber(IFile file, String token){
		IEditorInput ei = new FileEditorInput(file);
		try{
			int offset = findOffset(ei, token);
			IDocument doc = getDocument(ei);
			return doc.getLineOfOffset(offset) + 1;
		}catch(Exception e){
			e.printStackTrace();
		}

		return 0;
	}


	public static void openEditor(InkObject o){
		openEditor(o.reflect());
	}

	public static void openEditor(Mirror mirror){
		if (!mirror.isCoreObject()) {
			File f = mirror.getDescriptor().getResource();
			IFile file = getFile(f);
			IEditorInput ei = new FileEditorInput(file);
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().openEditor(ei, IDE.getEditorDescriptor(file.getName()).getId());
				revealInEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getActiveEditor(),
						findOffset(ei, "id=\"" + mirror.getShortId() + "\""),
						0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			IProject project = InkPlugin.getDefault().getInkProjects().get(0);
			try{
				if(!mirror.isClass()){
					mirror = mirror.getClassMirror();
				}
				ClassMirror cm = (ClassMirror)mirror;
				while(!cm.getJavaMapping().hasState() && cm.getSuper()!=null){
					cm = cm.getSuper();
				}
				String javaPath = cm.getFullJavaPackage();
				String javaFileName;
				if(cm.isStruct()){
					javaFileName = cm.getShortId() + ".java";
				}else{
					javaFileName = cm.getShortId() + InkNotations.Names.STATE_CLASS_EXTENSION + ".java";
				}
				IPath p = new Path(javaPath.replace(".", File.separator) + File.separatorChar + javaFileName);
				IJavaProject jProject = JavaCore.create(project);
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

	private static IDocument getDocument(IEditorInput ei) throws CoreException{
		IDocumentProvider dp = DocumentProviderRegistry.getDefault().getDocumentProvider(ei);
		dp.connect(ei);
		return dp.getDocument(ei);
	}

	private static int findOffset(IEditorInput ei, String token) throws Exception {
		IDocument doc = getDocument(ei);

		FindReplaceDocumentAdapter searcher= new FindReplaceDocumentAdapter(doc);
		IRegion r = searcher.find(0, token, true, true, false, false);
		if(r!=null){
			return r.getOffset();
		}
		return 0;
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
				offset += line.length() + 1;
			}
		} finally {
			reader.close();
		}
		if (found) {
			return offset;
		}
		return 0;
	}

	public static IContainer createFolder(IPath packagePath, IContainer outputFolder, boolean isSourceFolder)
			throws CoreException {
		if (packagePath.isEmpty()) {
			return outputFolder;
		}
		IFolder folder = outputFolder.getFolder(packagePath);
		if (!folder.exists()) {
			createFolder(packagePath.removeLastSegments(1), outputFolder, isSourceFolder);
			int flag;
			if(isSourceFolder){
				flag = IResource.FORCE;
			}else{
				flag = IResource.FORCE | IResource.DERIVED;
			}
			folder.create(flag, true, null);
		}
		return folder;
	}

}
