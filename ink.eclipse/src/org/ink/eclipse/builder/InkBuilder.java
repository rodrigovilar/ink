package org.ink.eclipse.builder;

import ink.eclipse.editors.page.DataBlock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ikayzo.sdl.SDLParseException;
import org.ink.core.utils.sdl.SdlParser;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InkErrorDetails;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VM;
import org.ink.core.vm.factory.VMMain;
import org.ink.core.vm.lang.InkObject;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.generators.Generator;
import org.ink.eclipse.generators.StateClassGenerator;
import org.ink.eclipse.utils.InkUtils;

public class InkBuilder extends IncrementalProjectBuilder {

	private static final IPath INK_DIR_PATH = new Path("src"+IPath.SEPARATOR +"main"+IPath.SEPARATOR+"dsl");
	private static final String DSL_DEF_FILENAME = "dsls.ink";

	private final InkErrorHandler errorHandler = new InkErrorHandler();

	String[] dsls;


	@Override
	protected void startupOnInitialize() {
		super.startupOnInitialize();
		VM vm = InkVM.instance();
		Set<String> nss = VMMain.getDsls();
		IFile f = getProject().getFile("dsls.ink");
		List<String> nssList = new ArrayList<String>();
		if(f.exists()){
			String path = f.getLocation().toFile().getAbsolutePath();
			for(String ns : nss){
				DslFactory factory = vm.getFactory(ns);
				File factoryConfFile = factory.getConfigurationFile();
				if(factoryConfFile!=null && factoryConfFile.getAbsolutePath().equals(path)){
					nssList.add(ns);
				}
			}
		}
		dsls = nssList.toArray(new String[]{});
	}

	class InkDeltaVisitor implements IResourceDeltaVisitor {

		IProgressMonitor monitor;

		public InkDeltaVisitor(IProgressMonitor monitor, List<DataBlock> changesElements){
			this.monitor = monitor;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if(resource.getType()==IResource.FILE){
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
					if(processInkFile(resource, monitor)){
						analyzeInkFile(resource);
					}
					break;
				}
			}
			//return true to continue visiting children.
			return true;
		}

		private void analyzeInkFile(IResource resource) {

		}
	}

	class InkResourceVisitor implements IResourceVisitor {

		IProgressMonitor monitor;

		public InkResourceVisitor(IProgressMonitor monitor){
			this.monitor = monitor;
		}

		@Override
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
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
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

	boolean processInkFile(IResource resource, IProgressMonitor monitor) {
		IPath resourcePath = resource.getProjectRelativePath();
		boolean result = false;
		if (resource instanceof IFile && resource.getName().endsWith(".ink")
				&& (resourcePath.uptoSegment(3).equals(INK_DIR_PATH)) ||
				DSL_DEF_FILENAME.equals(resourcePath.lastSegment())) {
			if(checkInkFile((IFile)resource)){
				moveInkFile((IFile)resource, monitor);
				result = true;
			}
		}
		return result;
	}

	private void moveInkFile(IFile file, IProgressMonitor monitor) {
		IFile existingFile = InkUtils.getOutputFile(getProject(), file);
		try{
			if(existingFile.exists()){
				existingFile.delete(true, null);
			}else{
				IPath folderPath = existingFile.getFullPath().removeLastSegments(1).removeFirstSegments(1);
				createFolder(folderPath, getProject());
			}
			file.copy(existingFile.getFullPath(), true, null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected IContainer createFolder(IPath packagePath, IContainer outputFolder) throws CoreException {
		if (packagePath.isEmpty()) {
			return outputFolder;
		}
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
		IFolder output = (IFolder) InkUtils.getJavaOutputFolder(getProject()).getParent();
		IFolder genFolder = output.getFolder("bin");
		if(genFolder.exists()){
			output.delete(true, new NullProgressMonitor());
		}
		getProject().accept(new InkResourceVisitor(monitor));
		List<InkErrorDetails> errors = InkPlugin.getDefault().reloadInk();
		processErrors(errors);
		Generator gen = new StateClassGenerator(output);
		Collection<InkObject> allClasses = InkUtils.getAllClasses(this.dsls);
		for(InkObject o : allClasses){
			gen.generate(o.reflect());
		}
	}


	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		List<DataBlock> changedElements = new ArrayList<DataBlock>();
		delta.accept(new InkDeltaVisitor(monitor, changedElements));
		List<InkErrorDetails> errors = InkPlugin.getDefault().reloadInk();
		processErrors(errors);
		Generator gen = new StateClassGenerator((IFolder) InkUtils.getJavaOutputFolder(getProject()).getParent());
		Collection<InkObject> allClasses = InkUtils.getAllClasses(this.dsls);
		for(InkObject o : allClasses){
			gen.generate(o.reflect());
		}
	}

	private void processErrors(List<InkErrorDetails> errors) {
		for(InkErrorDetails err : errors){
			try{
				String id = err.getId();
				id = "id=\"" + id.substring(id.indexOf(":") + 1, id.length()) + "\"";
				File f = err.getResource();
				int lineNumber = 0;
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
				try {
					String line;
					while ((line=reader.readLine())!=null){
						lineNumber++;
						if(line.contains(id)){
							break;
						}
					}
				}
				finally{
					reader.close();
				}
				IWorkspace workspace= ResourcesPlugin.getWorkspace();
				IPath location= Path.fromOSString(f.getAbsolutePath());
				IFile file= workspace.getRoot().getFileForLocation(location);
				addMarker(file, err.getFormattedMessage(), lineNumber, IMarker.SEVERITY_ERROR);
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
	}
}
