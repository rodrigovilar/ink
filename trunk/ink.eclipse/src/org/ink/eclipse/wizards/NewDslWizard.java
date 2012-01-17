package org.ink.eclipse.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InkVM;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.builder.InkBuilder;
import org.ink.eclipse.builder.InkNature;
import org.ink.eclipse.builder.ToggleNatureAction;
import org.ink.eclipse.utils.EclipseUtils;

/**
 * The new dsl wizard contains one page which creates
 * a new dsl declaration in a dsls.ink file for a specific project
 */

public class NewDslWizard extends Wizard implements INewWizard {
	private NewDslWizardPage page;
	private ISelection selection;

	private final static String FILE_NAME = "dsls.ink";
	private final static String TEMPLATE_NAME = "templates/dsl.template";

	private final static String NAME_TOKEN = "NAME";
	private final static String IMPORTS_TOKEN = "IMPORTS";
	private final static String NAMESPACE_TOKEN = "NAMESPACE";
	private final static String DSL_PACKAGE_TOKEN = "DSL_PACKAGE";
	private final static String DSL_DESCRIPTION_TOKEN = "DESCRIPTION";
	private final static String JAVA_PACKAGE_TOKEN = "JAVA_PACKAGE";

	/**
	 * Constructor for NewDslWizard.
	 */
	public NewDslWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	@Override
	public void addPages() {
		page = new NewDslWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final String containerName = page.getProjectName();

		// Creating tokens map between the name of the token as appears
		// in the DSL template and its value(getting from the page)

		final Map<String, String> tokens = new HashMap<String, String>();
		tokens.put(NAME_TOKEN, "\"" + getClassIdName() + "\"");
		tokens.put(IMPORTS_TOKEN, getImportsString());
		tokens.put(NAMESPACE_TOKEN, "\"" + page.getNamaspace() + "\"");
		tokens.put(DSL_PACKAGE_TOKEN, "\"" + page.getDslPackage() + "\"");
		tokens.put(DSL_DESCRIPTION_TOKEN, "\"" + page.getDslDescription() + "\"");
		tokens.put(JAVA_PACKAGE_TOKEN, "\"" + page.getJavaPackage() + "\"");

		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, tokens, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Builds Class id name from DSL namespace name
	 */
	String getClassIdName() {
		String namespace = page.getNamaspace();
		String className = "";
		int dotLoc = namespace.lastIndexOf('.');
		if (dotLoc != -1) {
			className = namespace.substring(dotLoc + 1, dotLoc + 2).toUpperCase() + namespace.substring(dotLoc + 2);
		} else {
			className = namespace;
		}
		className += "Factory";
		return className;

	}

	/**
	 * Builds IMPORTS token value
	 */
	String getImportsString() {
		StringBuilder importString = new StringBuilder();
		String[] importsList = page.getImportsList();
		Map<String, DslFactory> namespaceToDslFactoryMap = page.getNamespaceToDslFactoryMap();

		for (String importItem : importsList) {
			importString.append("import ref=\"").append(namespaceToDslFactoryMap.get(importItem).reflect().getId()).append("\"\n\t\t");
		}
		importString.append("import ref=\"ink.core:ObjectFactory\"\n");
		return importString.toString();
	}

	/**
	 * The worker method. It will find the project, create the dsls.ink
	 * file if missing or just add to its content, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(String containerName, Map<String, String> tokenMap, IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating dsl in " + FILE_NAME, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IProject)) {
			throwCoreException("Project \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		IFile dslfile = null;
		final IFile file = container.getFile(new Path(FILE_NAME));
		InputStream stream = null;
		try {
			stream = openContentStream(tokenMap);
			if (file.exists()) {
				// file.setContents(stream, true, true, monitor);

				file.appendContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			String dslPack = tokenMap.get(DSL_PACKAGE_TOKEN);
			if (dslPack != null) {
				String dslsFolder = InkBuilder.INK_DIR_PATH.toString() + IPath.SEPARATOR + dslPack.substring(1, dslPack.length() - 1).replace('.', IPath.SEPARATOR);
				IContainer cont = EclipseUtils.createFolder(new Path(dslsFolder), container, true);
				dslfile = cont.getFile(new Path("instances.ink"));
				String ns = tokenMap.get(NAMESPACE_TOKEN);
				ns = ns.substring(1, ns.length() - 1);
				String contents = "//" + ns + " DSL instances";
				InputStream source = new ByteArrayInputStream(contents.getBytes());
				dslfile.create(source, true, monitor);
				dslfile = cont.getFile(new Path("model.ink"));
				contents = "//" + ns + " DSL model elements";
				source = new ByteArrayInputStream(contents.getBytes());
				dslfile.create(source, true, monitor);
			}
			InkVM.instance().introduceNewDSl(file.getLocation().toFile().getAbsolutePath());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		IProject p = (IProject) resource;
		if (!p.hasNature(InkNature.NATURE_ID)) {
			ToggleNatureAction action = new ToggleNatureAction();
			action.toggleNature(p);
		}
		final IFile tragetFile = dslfile;
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, tragetFile, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	/**
	 * Builds file contents with template contents replaced by tokens values
	 */

	private InputStream openContentStream(Map<String, String> tokenMap) throws CoreException {

		StringBuffer sb = new StringBuffer();
		sb.append(System.getProperty("line.separator"));

		// Creating resolver with the tokeMap built earlier
		MapTokenResolver resolver = new MapTokenResolver(tokenMap);
		URL templateUrl = InkPlugin.getDefault().getBundle().getResource(TEMPLATE_NAME);
		try {
			InputStream input = templateUrl.openStream();

			// Getting template content with tokens real values (according to
			// tokenMap)
			Reader reader = new TokenReplacingReader(new InputStreamReader(input), resolver);
			try {
				int data = reader.read();
				while (data != -1) {
					sb.append((char) data);
					data = reader.read();
				}
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, "newDSLWizard", IStatus.OK, e.getLocalizedMessage(), null);
			throw new CoreException(status);
		}

		return new ByteArrayInputStream(sb.toString().getBytes());

	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "ink.eclipse", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}