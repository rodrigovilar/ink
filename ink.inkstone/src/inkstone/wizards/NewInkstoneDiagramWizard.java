package inkstone.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
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
		"<!-- Created with INKSTONE Eclipse plug-in (Addition to the INK framework - http://code.google.com/a/eclipselabs.org/p/ink/) -->",
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
					doFinish(page_.getDiagramFolderPath(), page_.getDiagramFilePath(), page_.getInkFilePath(), monitor);
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
	
	private void doFinish(IPath diagramFolder, IPath diagramFile, IPath inkFile, IProgressMonitor monitor) throws CoreException {
		IFolder diagFolder;
		IFile   diagFile;
		
		monitor.beginTask("Create new InkStone diagram file.", 3);

		// Check input
		diagFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(diagramFolder);
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
		
		monitor.done();
	}

}
