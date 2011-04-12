package org.ink.eclipse;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkErrorDetails;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VMConfig;
import org.ink.core.vm.factory.VMMain;
import org.ink.core.vm.factory.internal.CoreInstantiationStrategy;
import org.ink.eclipse.builder.InkNature;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class InkPlugin extends AbstractUIPlugin {

	public static final String INK_HOME = "INK_HOME";

	// The plug-in ID
	public static final String PLUGIN_ID = "ink.eclipse"; //$NON-NLS-1$

	public static final String EDITOR_ID = "ink.eclipse.editors.InkEditor";

	// The shared instance
	private static InkPlugin plugin;

	private Context inkContext;

	private int maxBuildIterations = 10;

	/**
	 * The constructor
	 */
	public InkPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		maxBuildIterations = ws.getDescription().getMaxBuildIterations();
		addInkClasspath();
		loadInk();
	}

	public int getMaxBuildIterations(){
		return maxBuildIterations;
	}

	private void addInkClasspath() {
		try {
			URL url = new URL(getBundle().getLocation());
			String bundleLocation = url.getPath();
			bundleLocation = bundleLocation.substring("file:".length(), bundleLocation.length());
			IPath p = Path.fromOSString(bundleLocation);
			System.out.println(p.toFile().getAbsolutePath());
			p = Path.fromOSString(p.toFile().getAbsolutePath());
			JavaCore.setClasspathVariable(INK_HOME, p, new NullProgressMonitor());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<InkErrorDetails> reloadInk(){
		VMMain.restart();
		inkContext = InkVM.instance().getContext();
		return InkVM.instance().collectErrors();
	}

	private void loadInk(){
		VMConfig.setInstantiationStrategy(new CoreInstantiationStrategy());
		List<IProject> inkProjects = getInkProjects();
		List<String> paths = new ArrayList<String>();
		IFile f;
		for(IProject p : inkProjects){
			f = p.getFile("dsls.ink");
			if(f!=null){
				paths.add(f.getLocation().toFile().getAbsolutePath());
			}
		}
		inkContext = InkVM.instance(null, paths.toArray(new String[]{})).getContext();
	}

	public Context getInkContext(){
		return inkContext;
	}

	public List<IProject> getInkProjects(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<IProject> inkProjects = new ArrayList<IProject>();
		for(IProject p : projects){
			try {
				if(p.isOpen() && p.hasNature(InkNature.NATURE_ID)){
					inkProjects.add(p);
				}
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
		return inkProjects;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		VMMain.stop();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static InkPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
