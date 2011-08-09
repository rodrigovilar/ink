package org.ink.eclipse.builder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.EclipseUtils;

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
	@Override
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		IJavaProject jProject = JavaCore.create(project);
		try {
			IPath inkHome = JavaCore.getClasspathVariable(InkPlugin.INK_HOME);
			IPath libFolder = inkHome.append("lib");

			File f = libFolder.toFile();
			System.out.println(f.getAbsolutePath());
			IFolder outputFolder = EclipseUtils.getJavaOutputFolder(project);
			IFolder genFolder;
			IFolder binFolder;
			if(outputFolder.getParent().getType()==IResource.PROJECT){
				genFolder = outputFolder.getFolder("gen");
				if(!genFolder.exists()){
					genFolder.create(IResource.FORCE | IResource.DERIVED, true, null);
				}
				binFolder = outputFolder.getFolder("bin");
				if(!binFolder.exists()){
					binFolder.create(IResource.FORCE | IResource.DERIVED, true, null);
				}
				jProject.setOutputLocation(binFolder.getFullPath(), new NullProgressMonitor());
			}else{
				binFolder = outputFolder;
				genFolder = ((IFolder)outputFolder.getParent()).getFolder("gen");
			}

			IPath genPath = genFolder.getFullPath();
			File[] jars = f.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(".jar")){
						return true;
					}
					return false;
				}
			});
			List<File> jarsToAdd = new ArrayList<File>(jars.length);
			IClasspathEntry[] entries = jProject.getRawClasspath();
			boolean foundGenClasspath = false;
			for(File j : jars){
				boolean found = false;
				for(IClasspathEntry en : entries){
					if(en.getPath().toFile().getName().equals(j.getName())){
						found = true;
					}else if(en.getPath().toFile().getName().equals(genPath.toFile().getName())){
						foundGenClasspath = true;
					}
				}
				if(!found){
					jarsToAdd.add(j);
				}
			}
			List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(Arrays.asList(entries));
			File j;
			IClasspathEntry cpe;
			int i=0;
			IPath jarPath;
			IPath pathToSet;
			for(;i<jarsToAdd.size();i++){
				j = jarsToAdd.get(i);
				jarPath = new Path(j.getAbsolutePath()).makeRelativeTo(inkHome);
				pathToSet = new Path(InkPlugin.INK_HOME);
				pathToSet = pathToSet.append(jarPath);
				cpe = JavaCore.newVariableEntry(pathToSet, null, null);
				newEntries.add(cpe);
			}
			if(!foundGenClasspath){
				cpe = JavaCore.newSourceEntry(genPath);
				newEntries.add(cpe);
			}
			jProject.setRawClasspath(newEntries.toArray(new IClasspathEntry[]{}), new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
		}


		ICommand[] commands = desc.getBuildSpec();

		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(InkBuilder.BUILDER_ID)) {
				return;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 1, commands.length);
		ICommand command = desc.newCommand();
		command.setBuilderName(InkBuilder.BUILDER_ID);
		newCommands[0] = command;
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	@Override
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
	@Override
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
