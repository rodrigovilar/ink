package org.ink.eclipse.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ikayzo.sdl.SDLParseException;
import org.ink.core.utils.sdl.SdlParser;
import org.ink.core.vm.constraints.ResourceType;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidationMessage;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InkErrorDetails;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.InkNotations;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.cache.Java2InkMappings;
import org.ink.eclipse.generators.EnumGenerator;
import org.ink.eclipse.generators.Generator;
import org.ink.eclipse.generators.StateClassGenerator;
import org.ink.eclipse.utils.EclipseUtils;
import org.ink.eclipse.utils.InkUtils;
import org.ink.eclipse.vm.EclipseResourceResolver;

public class InkBuilder extends IncrementalProjectBuilder {

	public static final IPath INK_DIR_PATH = new Path("src" + IPath.SEPARATOR + "main" + IPath.SEPARATOR + "dsl");
	public static final IPath INK_TEST_DIR_PATH = new Path("src" + IPath.SEPARATOR + "test" + IPath.SEPARATOR + "dsl");
	private static final String DSL_DEF_FILENAME = "dsls.ink";
	private static final EclipseResourceResolver RR = new EclipseResourceResolver();

	private final InkErrorHandler errorHandler = new InkErrorHandler();
	private final List<IFile> changedInkFiles = new ArrayList<IFile>();
	private final Map<IFile, String> changedJavaFiles = new HashMap<IFile, String>();
	private boolean fullBuild = false;

	@Override
	protected void startupOnInitialize() {
		super.startupOnInitialize();
	}

	class InkDeltaVisitor implements IResourceDeltaVisitor {

		private final IProgressMonitor monitor;

		public InkDeltaVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE) {
				switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					// handle added resource
					processInkFile(resource, monitor);
					processJavaFile(resource, monitor);
					break;
				case IResourceDelta.REMOVED:
					// handle removed resource
					processJavaFile(resource, monitor);
					break;
				case IResourceDelta.CHANGED:
					// handle changed resource
					processInkFile(resource, monitor);
					processJavaFile(resource, monitor);
					break;
				}
			}
			// return true to continue visiting children.
			return true;
		}

	}

	class InkResourceVisitor implements IResourceVisitor {

		IProgressMonitor monitor;

		public InkResourceVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		@Override
		public boolean visit(IResource resource) {
			processInkFile(resource, monitor);
			// return true to continue visiting children.
			return true;
		}
	}

	class InkErrorHandler {

		private void addMarker(IFile file, String msg, int lineNumber, int severity) {
			InkBuilder.this.addMarker(file, msg, lineNumber, severity);
		}

		public void error(IFile file, String msg, int lineNumber) {
			addMarker(file, msg, lineNumber, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(IFile file, String msg, int lineNumber) {
			addMarker(file, msg, lineNumber, IMarker.SEVERITY_ERROR);
		}

		public void warning(IFile file, String msg, int lineNumber) {
			addMarker(file, msg, lineNumber, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "ink.eclipse.inkBuilder";

	private static final String MARKER_TYPE = "ink.eclipse.inkProblem";

	private void addMarker(IResource file, String message, int lineNumber, int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			marker.setAttribute(IMarker.SOURCE_ID, "INK");
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
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor) throws CoreException {
		try {
			//in full build don't copy ink resources since the Java builder will delete them
			//only in the next incremental build (always happens after full build) copy the files.
			if (kind == FULL_BUILD) {
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
					changedInkFiles.clear();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			changedInkFiles.clear();
			addMarker(getProject(), "internal error.", 0, IMarker.SEVERITY_ERROR);
		}
		return getProject().getReferencedProjects();
	}

	boolean processInkFile(IResource resource, IProgressMonitor monitor) {
		IPath resourcePath = resource.getProjectRelativePath();
		boolean result = false;
		if (resource.getName().endsWith(".ink") && 
				(resourcePath.uptoSegment(3).equals(INK_DIR_PATH) || resourcePath.uptoSegment(3).equals(INK_TEST_DIR_PATH)) || 
				(DSL_DEF_FILENAME.equals(resourcePath.lastSegment()) && resourcePath.segments().length == 1)) {
			if (checkInkFile((IFile) resource)) {
				changedInkFiles.add((IFile) resource);
				result = true;
			}
		}
		return result;
	}

	private void processJavaFile(IResource resource, IProgressMonitor monitor) {
		try {
			Object inkId = Java2InkMappings.get(resource.getFullPath().toOSString());
			if (inkId != null) {
				changedJavaFiles.put((IFile) resource, inkId.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void moveInkFile(IFile file, IProgressMonitor monitor) {
		IFile existingFile = EclipseUtils.getOutputFile(getProject(), file);
		try {
			File fsFile = new File(existingFile.getLocationURI());
			if (fsFile.exists()) {
				existingFile.delete(true, monitor);
			} else {
				IPath folderPath = existingFile.getFullPath().removeLastSegments(1).removeFirstSegments(1);
				EclipseUtils.createFolder(folderPath, getProject(), false);
			}
			file.copy(existingFile.getFullPath(), true, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean checkInkFile(IFile file) {
		deleteMarkers(file);
		try {
			SdlParser.parse(file.getLocationURI().toURL());
		} catch (SDLParseException e) {
			errorHandler.error(file, e.getMessage(), e.getLine());
			return false;
		} catch (Exception e1) {
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

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		// todo - this is a hack until I fix that project full
		// build causes the kernel to restart
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);

		IFolder output = (IFolder) EclipseUtils.getJavaOutputFolder(getProject()).getParent();
		IFolder genFolder = output.getFolder("gen");
		if (genFolder.exists()) {
			genFolder.delete(true, new NullProgressMonitor());
		}
		genFolder.create(IResource.FORCE | IResource.DERIVED, true, null);
		fullBuild = true;
		getProject().accept(new InkResourceVisitor(monitor));
		List<InkErrorDetails> errors = reloadProjectDSLs();
		processErrors(errors, monitor);
		genrateJavaFiles(output);
	}

	private List<InkErrorDetails> reloadProjectDSLs() {
		Map<String, DslFactory> factories = InkUtils.getProjectDSLFactories(getProject());
		List<InkErrorDetails> result = new ArrayList<InkErrorDetails>();
		if (factories.size() > 0) {
			List<DslFactory> sortedFactories = new ArrayList<DslFactory>(factories.values());
			Collections.sort(sortedFactories);
			for (int i=sortedFactories.size()-1;i>=0;i--) {
				DslFactory factory = sortedFactories.get(i);
				Set<String> scope = factory.getScope();
				boolean reload = true;
				if(!fullBuild){
					for(String ns : scope){
						if(!ns.equals(factory.getNamespace()) && factories.containsKey(ns)){
							reload = false;
							break;
						}
					}
				}
				if(reload){
					String ns = factory.getNamespace();
					InkVM.instance().reloadDSL(ns, !fullBuild);
					result.addAll(InkVM.instance().collectErrors());
				}
			}
		} else {
			IFile f = getProject().getFile("dsls.ink");
			try {
				InkVM.instance().introduceNewDSl(f.getLocation().toFile().getAbsolutePath());
				String[] nss = InkUtils.getProjectNamespaces(getProject());
				for (String ns : nss) {
					result.addAll(InkVM.instance().collectErrors(ns));
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
		return result;
	}
	
	private List<InkErrorDetails> reloadDsl(IFile changedFile) throws CoreException {
		DslFactory factory = InkUtils.getFactory(changedFile.getLocation().toFile());
		List<InkErrorDetails> result = new ArrayList<InkErrorDetails>();
		if (factory!=null) {
			String ns = factory.getNamespace();
			List<DslFactory> factories = new ArrayList<DslFactory>(factory.getDependentFactories());
			factories.add(factory);
			for(DslFactory f : factories){
				for(File file : f.getSourceFiles()){
					try{
						IFile ifile = EclipseUtils.getEclipseFile(file);
						ifile.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
					}catch(Throwable e){
						e.printStackTrace();
					}
				}
			}
			InkVM.instance().reloadDSL(ns, true);
			for(DslFactory f : factories){
				result.addAll(InkVM.instance().collectErrors(f.getNamespace()));
			}
		}
		return result;
	}

	private void genrateJavaFiles(IFolder output) {
		Generator gen = new StateClassGenerator(output);
		String[] dsls = InkUtils.getProjectNamespaces(getProject());
		Collection<Mirror> all = InkUtils.getAllClasses(dsls);
		for (Mirror o : all) {
			try {
				ClassMirror cm = (ClassMirror) o;
				mapJavaToInk(cm);
				gen.generate(cm);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		gen = new EnumGenerator(output);
		all = InkUtils.getInstances(dsls, CoreNotations.Ids.ENUM_TYPE, true, true);
		for (Mirror o : all) {
			gen.generate(o);
		}
	}

	private void mapJavaToInk(ClassMirror cm) {
		IFile f = EclipseUtils.getJavaBehaviorFile(cm);
		if (f != null) {
			addJavaToInkMapping(cm, f);
		}
		f = EclipseUtils.getJavaInterfaceFile(cm);
		if (f != null) {
			addJavaToInkMapping(cm, f);
		}
	}

	private void addJavaToInkMapping(ClassMirror cm, IFile f) {
		IPath p = EclipseUtils.getJavaCompiledClass(f.getProject(), f);
		Java2InkMappings.put(p.toOSString(), cm.getId());
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new InkDeltaVisitor(monitor));
		List<InkErrorDetails> errors = null;
		if (!changedInkFiles.isEmpty()) {
			for (IFile f : changedInkFiles) {
				moveInkFile(f, monitor);
			}
			if (!fullBuild) {
				if(changedInkFiles.size()>1){
					getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
					errors = reloadProjectDSLs();
					processErrors(errors, monitor);
					IFolder outputFolder = (IFolder) EclipseUtils.getJavaOutputFolder(getProject()).getParent();
					genrateJavaFiles(outputFolder);
				}else{
					//very ugly hack - do real incremental
					errors = reloadDsl(changedInkFiles.get(0));
					processErrors(errors, monitor);
					IFolder outputFolder = (IFolder) EclipseUtils.getJavaOutputFolder(getProject()).getParent();
					genrateJavaFiles(outputFolder);
				}
			}
			changedInkFiles.clear();
			fullBuild = false;
		} else if (!changedJavaFiles.isEmpty()) {
			Context context = InkPlugin.getDefault().getInkContext();
			ValidationContext vc = context.newInstance(CoreNotations.Ids.VALIDATION_CONTEXT).getBehavior();
			for (Map.Entry<IFile, String> en : changedJavaFiles.entrySet()) {
				InkObjectState o = context.getState(en.getValue(), false);
				if (o != null) {
					String filename = en.getKey().getFullPath().removeFileExtension().lastSegment();
					IFile sourceFile;
					if (filename.endsWith(InkNotations.Names.BEHAVIOR_EXTENSION)) {
						sourceFile = EclipseUtils.getJavaBehaviorFile((ClassMirror) o.reflect());
					} else {
						sourceFile = EclipseUtils.getJavaInterfaceFile((ClassMirror) o.reflect());
					}
					if (sourceFile != null && sourceFile.exists()) {
						sourceFile.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_ZERO);
					}
					File inkFile = o.reflect().getDescriptor().getResource();
					IFile eclipseInkFile = EclipseUtils.getEclipseFile(inkFile);
					if (eclipseInkFile.exists()) {
						eclipseInkFile.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_ZERO);
					}
					List<String> ids = context.getFactory().getElements(inkFile.getAbsolutePath());
					if (ids != null) {
						for (String id : ids) {
							o = context.getState(id);
							o.validate(vc);
							processErrors(o.reflect(), vc.getMessages(), monitor);
							vc.reset();
						}
					}
				}
			}
			changedJavaFiles.clear();
			fullBuild = false;
		}

	}

	private void processErrors(Mirror mirror, List<ValidationMessage> messages, IProgressMonitor monitor) {
		if (messages != null && !messages.isEmpty()) {
			List<InkErrorDetails> errors = new ArrayList<InkErrorDetails>();
			for (ValidationMessage vm : messages) {
				InkErrorDetails err = new InkErrorDetails(mirror.getId(), vm.getErrorPath(), vm.getFormattedMessage(), mirror.getDescriptor().getResource(), vm.getResourceType());
				errors.add(err);
			}
			processErrors(errors, monitor);
		}
	}

	private void processErrors(List<InkErrorDetails> errors, IProgressMonitor monitor) {
		if (!errors.isEmpty()) {
			for (InkErrorDetails err : errors) {
				try {
					String id = err.getId();
					switch (err.getResourceType()) {
					case INK:
						id = "id=\"" + id.substring(id.indexOf(":") + 1, id.length()) + "\"";
						File f = err.getInkSourceDefinition();
						int lineNumber = EclipseUtils.findLineNumber(EclipseUtils.getFile(f), id);
						IFile file = EclipseUtils.getEclipseFile(f);
						addMarker(file, err.getFormattedMessage(), lineNumber, IMarker.SEVERITY_ERROR);
						break;
					default:
						InkObjectState o = InkPlugin.getDefault().getInkContext().getState(id, false);
						if (o != null) {
							ClassMirror cm = o.reflect();
							IFile javaFile;
							if (err.getResourceType() == ResourceType.JAVA_CLASS) {
								javaFile = EclipseUtils.getJavaBehaviorFile(cm);
							} else {
								javaFile = EclipseUtils.getJavaInterfaceFile(cm);

							}
							deleteMarkers(javaFile);
							addMarker(javaFile, err.getFormattedMessage(), 1, IMarker.SEVERITY_ERROR);
						}
						break;

					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}
}
