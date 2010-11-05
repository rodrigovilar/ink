package org.ink.eclipse.builder;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.ink.eclipse.InkPlugin;

public class InkNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "ink.eclipse.inkNature";

	private IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		IJavaProject jProject = JavaCore.create(project);
		String bundleLocation;
		try {
			URL url = new URL(InkPlugin.getDefault().getBundle().getLocation());
			bundleLocation = url.getPath();
			System.out.println(bundleLocation);
			bundleLocation = bundleLocation.substring("file:".length(), bundleLocation.length());
			System.out.println(bundleLocation);
			File f = new File(bundleLocation + File.separatorChar +"lib");
			System.out.println(f.getAbsolutePath());
			File[] jars = f.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(".jar")){
						return true;
					}
					return false;
				}
			});
			System.out.println(jars);
			List<File> jarsToAdd = new ArrayList<File>(jars.length);
			IClasspathEntry[] entries = jProject.getRawClasspath();
			for(File j : jars){
				boolean found = false;
				for(IClasspathEntry en : entries){
					if(en.getPath().toFile().getName().equals(j.getName())){
						found = true;
					}
				}
				if(!found){
					jarsToAdd.add(j);
				}
			}
			if(!jarsToAdd.isEmpty()){
				IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + jarsToAdd.size()];
				System.arraycopy(entries, 0, newEntries, 0, entries.length);
				File j;
				IClasspathEntry cpe;
				for(int i=0;i<jarsToAdd.size();i++){
					j = jarsToAdd.get(i);
					cpe = JavaCore.newLibraryEntry(new Path(j.getAbsolutePath()), null, null);
					newEntries[i+entries.length] = cpe;
				}
				jProject.setRawClasspath(newEntries, new NullProgressMonitor());
				
			}
			//jProject.getRawClasspath()
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
		ICommand[] commands = desc.getBuildSpec();

		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(InkBuilder.BUILDER_ID)) {
				return;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		ICommand command = desc.newCommand();
		command.setBuilderName(InkBuilder.BUILDER_ID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(InkBuilder.BUILDER_ID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
						commands.length - i - 1);
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);			
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

}
