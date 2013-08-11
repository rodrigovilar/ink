package inkstone.wizards;

import java.lang.reflect.InvocationTargetException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class NewInkstoneDiagramWizard extends Wizard implements INewWizard {

	private NewInkstoneDiagramWizardPage page_;
	private ISelection selection_;
	private static String[] svgDiagramTemplate = {
		"<svg width=\"640\" height=\"480\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n",
		"\n",
		"<!-- Created with INKSTONE Eclipse plug-in (Addition to the INK framework - http://code.google.com/a/eclipselabs.org/p/ink/) -->" + "\n",
		"\n",
		"\n",
		"\n",
		"<g opacity=\"0.25\">\n",
		"	<title>Background Layer</title>\n",
		"	<image xlink:href=\"src\\utils\\gallery\\inkstone_big_logo.jpg\" id=\"inkstone_big_logo\" height=\"100\" width=\"100\" y=\"300\" x=\"500\"/>\n",
		"</g>\n",
		"\n",
		"</svg>\n"		
	};
	
	public NewInkstoneDiagramWizard() {
		setWindowTitle("New INK Diagram");
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page_ = new NewInkstoneDiagramWizardPage(selection_);
		addPage(page_);
	}

	/**
	 * Accept the selection in the workbench to see if can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection_ = selection;
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard.
	 * Here the wizard starts a new thread with progress bar GUI, in order to create a new diagram svg file (*.isd file) 
	 */
	@Override
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(page_.getDiagramFolderPath(), page_.getDiagramFileName(), page_.getInkFilePath(), monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(false, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	private void doFinish(IPath diagramFolder, String diagramFile, IPath inkFilePath, IProgressMonitor monitor) throws CoreException {
		IFile	testInkFile;
		IFile	testDiagramFile;
		
		monitor.beginTask("Create new InkStone diagram file.", 2);

		// Step 1 : Set default ink file:
		testInkFile = (IFile)ResourcesPlugin.getWorkspace().getRoot().findMember(inkFilePath);		
		if( testInkFile == null ) {
			String initialText = "// INK Diagram auto generated file \n\n";
			String inkFileprojectName = inkFilePath.segment(0);
			String inkFileName = inkFilePath.lastSegment();
			String relInkFileFolderLocation = ""; 
			for(int i=1; i < (inkFilePath.segments().length - 1); i++) {
				relInkFileFolderLocation += inkFilePath.segment(i) + "/";
			}
			IProject targetInkFileProject = ResourcesPlugin.getWorkspace().getRoot().getProject(inkFileprojectName);
			if (targetInkFileProject.exists() && !targetInkFileProject.isOpen()) {
				targetInkFileProject.open(null);
			}
			IFolder inkFileFolder = targetInkFileProject.getFolder(relInkFileFolderLocation);
			if (inkFileFolder.exists()) {
				// create a new file
				IFile newInkFile =  inkFileFolder.getFile(new Path(inkFileName));
				InputStream inkFileStream = new ByteArrayInputStream(initialText.getBytes());
				newInkFile.create(inkFileStream, true, monitor);				   
			}
		}
		
		monitor.worked(1);
		
		// Step 2 : Create isd file
		IPath diagramFilePath = new Path(diagramFolder.toString() + "/" + diagramFile); 
		testDiagramFile = (IFile)ResourcesPlugin.getWorkspace().getRoot().findMember(diagramFilePath);
		if( testDiagramFile == null ) {
			svgDiagramTemplate[4] = "<defaultInkFile value=" + inkFilePath.toOSString() + ">\n";
			String diagramProjectName = diagramFolder.segment(0);
			IProject targetDiagramProject = ResourcesPlugin.getWorkspace().getRoot().getProject(diagramProjectName);
			String relDiagramFileFolderLocation = ""; 
			for(int i=1; i < (diagramFilePath.segments().length - 1); i++) {
				relDiagramFileFolderLocation += diagramFilePath.segment(i) + "/";
			}
			IFolder diagramFileFolder = targetDiagramProject.getFolder(relDiagramFileFolderLocation);
			if (diagramFileFolder.exists()) {
				// create a new file
				IFile newDiagramFile = diagramFileFolder.getFile(new Path(diagramFile));
				StringBuilder sb = new StringBuilder();
			    for(String s : svgDiagramTemplate){
			        sb.append(s);           
			    }
				InputStream diagramFileStream = new ByteArrayInputStream(sb.toString().getBytes());
				newDiagramFile.create(diagramFileStream, true, monitor);
			}
		}
		
		/*
		// Check input
		diagFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(diagramFolder);
		if( !diagFolder.exists() ) {
			try {
				diagFolder.create(true, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if( diagFolder.exists() ) {
			diagFile = diagFolder.getFile(diagramFile);
			if (!diagFile.exists()) {
				StringBuilder sb = new StringBuilder();
			    for(String s : svgDiagramTemplate){
			        sb.append(s);           
			    }
				try {
					InputStream source = new ByteArrayInputStream( sb.toString().getBytes("UTF-8") );
					diagFile.create(source, IResource.NONE, null);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				//
			} else {
				// ToDo : Ask the user if to overwrite existing diagram
			}
		} else {
			//ToDo : ask the user if to create this folder....
		}

		monitor.worked(2);
		// Create SVG file
		try{
			
			FileWriter fstream = new FileWriter("out.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			for(int i=0; i<svgDiagramTemplate.length; i++) {
				out.write(svgDiagramTemplate[i]);
			}
			out.close();
			monitor.worked(3);
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		*/
		
		monitor.done();
	}

}
