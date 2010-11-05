package org.ink.eclipse.builder;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.ikayzo.sdl.SDLParseException;
import org.ink.core.utils.sdl.SdlParser;

public class InkBuilder extends IncrementalProjectBuilder {
	
	private static final IPath INK_DIR_PATH = new Path("src"+IPath.SEPARATOR +"main"+IPath.SEPARATOR+"dsl");
	private static final String DSL_DEF_FILENAME = "dsls.ink";
	
	private InkErrorHandler errorHandler = new InkErrorHandler();
	
	class InkDeltaVisitor implements IResourceDeltaVisitor {
		
		IProgressMonitor monitor;
		
		public InkDeltaVisitor(IProgressMonitor monitor){
			this.monitor = monitor;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				processInkFile(resource, monitor);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				processInkFile(resource, monitor);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class InkResourceVisitor implements IResourceVisitor {
		
		IProgressMonitor monitor;
		
		public InkResourceVisitor(IProgressMonitor monitor){
			this.monitor = monitor;
		}
		
		public boolean visit(IResource resource) {
			processInkFile(resource, monitor);
			//return true to continue visiting children.
			return true;
		}
	}
	
	class InkErrorHandler{
		
		private void addMarker(IFile file, String msg, int lineNumber, int severity) {
			InkBuilder.this.addMarker(file, msg, lineNumber, severity);
		}

		public void error(IFile file, String msg, int lineNumber){
			addMarker(file, msg, lineNumber, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(IFile file, String msg, int lineNumber){
			addMarker(file, msg, lineNumber, IMarker.SEVERITY_ERROR);
		}

		public void warning(IFile file, String msg, int lineNumber){
			addMarker(file, msg, lineNumber, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "ink.eclipse.inkBuilder";

	private static final String MARKER_TYPE = "ink.eclipse.inkProblem";

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void processInkFile(IResource resource, IProgressMonitor monitor) {
		IPath resourcePath = resource.getProjectRelativePath();
		if (resource instanceof IFile && resource.getName().endsWith(".ink") 
				&& (resourcePath.uptoSegment(3).equals(INK_DIR_PATH)) || 
				DSL_DEF_FILENAME.equals(resourcePath.lastSegment())) {
			if(checkInkFile((IFile)resource)){
				moveInkFile((IFile)resource, monitor);
			}
		}
	}
	
	private void moveInkFile(IFile file, IProgressMonitor monitor) {
		IJavaProject jProject = JavaCore.create(getProject());
		try {
			IPath outputPath = jProject.getOutputLocation().removeFirstSegments(1);
			IPath relativeFilepath = file.getFullPath().removeFirstSegments(4);
			if(relativeFilepath.isEmpty()){
				relativeFilepath = file.getFullPath().removeFirstSegments(1);
			}
			IFolder outputFolder = getProject().getFolder(outputPath);
			IFile existingFile = outputFolder.getFile(relativeFilepath);
			if(existingFile.exists()){
				existingFile.delete(true, null);
			}else{
				IPath folderPath = existingFile.getFullPath().removeLastSegments(1).removeFirstSegments(1);
				createFolder(folderPath, getProject());
			}
			file.copy(existingFile.getFullPath(), true, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected IContainer createFolder(IPath packagePath, IContainer outputFolder) throws CoreException {
		if (packagePath.isEmpty()) return outputFolder;
		IFolder folder = outputFolder.getFolder(packagePath);
		if (!folder.exists()) {
			createFolder(packagePath.removeLastSegments(1), outputFolder);
			folder.create(IResource.FORCE | IResource.DERIVED, true, null);
		}
		return folder;
	}

	boolean checkInkFile(IFile file) {
		deleteMarkers(file);
		try {
			SdlParser.parse(file.getLocationURI().toURL());
		}catch(SDLParseException e){
			errorHandler.error(file, e.getMessage(), e.getLine());
			return false;
		}
		catch (Exception e1) {
			errorHandler.error(file, "Could not parse file.", 0);
			return false;
		}
		return true;
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new InkResourceVisitor(monitor));
		} catch (CoreException e) {
		}
	}


	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new InkDeltaVisitor(monitor));
	}
}
