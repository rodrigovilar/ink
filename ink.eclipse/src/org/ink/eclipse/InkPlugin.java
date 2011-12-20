package org.ink.eclipse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.ink.core.utils.StringUtils;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkErrorDetails;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VMConfig;
import org.ink.core.vm.factory.VMMain;
import org.ink.eclipse.builder.InkNature;
import org.ink.eclipse.cache.Java2InkMappings;
import org.ink.eclipse.vm.EclipseResourceResolver;
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
		try{
			File f = getBundle().getDataFile("ink_mapping.csv");
			if(f!=null && f.exists()){
				BufferedReader reader  = new BufferedReader(new InputStreamReader(f.toURI().toURL().openStream(), "UTF-8"));
				try{
					String line;
					while((line=reader.readLine())!=null){
						int loc = line.indexOf(",");
						String javaId = line.substring(0, loc);
						String inkId = line.substring(loc+1, line.length());
						Java2InkMappings.put(javaId, inkId);
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(reader!=null){
						try{
							reader.close();
						}catch(Throwable e){}
					}
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
		}
		plugin = this;
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		maxBuildIterations = ws.getDescription().getMaxBuildIterations();
		addInkClasspath();
		startInkVM();
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
			System.out.println("Bundle Location : " + p.toFile().getAbsolutePath());
			p = Path.fromOSString(p.toFile().getAbsolutePath());
			JavaCore.setClasspathVariable(INK_HOME, p, new NullProgressMonitor());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<InkErrorDetails> reloadInk(){
		startInkVM();
		return InkVM.instance().collectErrors();
	}

	public void startInkVM(){
		InkVM.destroy();
		VMConfig.setInstantiationStrategy(new EclipseResourceResolver());
		List<IProject> inkProjects = getInkProjects();
		List<String> paths = new ArrayList<String>();
		IFile f;
		for(IProject p : inkProjects){
			f = p.getFile("dsls.ink");
			if(f!=null){
				paths.add(f.getLocation().toFile().getAbsolutePath());
			}
		}
		InkVM.instance(null, paths.toArray(new String[]{})).getContext();
	}

	public Context getInkContext(){
		return InkVM.instance().getContext();
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

	public List<IProject> getInkProjects(IProject dependentProject){
		List<IProject> inkProjects = new ArrayList<IProject>();
		if(dependentProject.isOpen()){
			try {
				IProject[]  projects = dependentProject.getReferencedProjects();
				for(IProject p : projects){
					if(p.isOpen() && p.hasNature(InkNature.NATURE_ID)){
						inkProjects.add(p);
					}
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
		VMMain.stop();
		File f = getBundle().getDataFile("ink_mapping.csv");
		if(f.exists()){
			f.delete();
		}
		f.createNewFile();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		try{
			Iterator<Map.Entry<String, String>> iter = Java2InkMappings.iterate();
			while(iter.hasNext()){
				Map.Entry<String, String> en = iter.next();
				writer.write(en.getKey() +","+en.getValue() +StringUtils.LINE_SEPARATOR);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				writer.flush();
				writer.close();
			}catch(Throwable e){}
		}
		super.stop(context);
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
