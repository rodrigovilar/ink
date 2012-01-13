package org.ink.core.vm.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ink.core.vm.exceptions.InkException;
import org.ink.core.vm.modelinfo.ModelInfoFactory;

/**
 * The entry point to the Ink Virtual Machine.
 * <br/>
 * Use the {@link #instance()} method to retrieve the singleton instance of <code>InkVM</code>.
 * <br/>
 * Example:
 * <pre>
 *     InkVM.instance().getContext().getObject("ink.tutorial1:Active_offers");
 * </pre>
 *
 * @author Lior Schachter
 */
public class InkVM implements VM {

	private static VM vm = null;

	/**
	 * Returns the singleton instance of the Ink Virtual Machine.
	 * Use this method to retrieve the entry point to the Ink VM, which allows fetching of {@link Context} and {@link DslFactory} objects.
	 * <br/>
	 * Example:
	 * <pre>
	 *     InkVM.instance().getContext().getState("ink.tutorial1:Active_offers").getBehavior();
	 * </pre>
	 * @return the singleton instance of the Ink Virtual Machine.
	 */
	public static VM instance() {
		return instance(null, null);
	}

	/**
	 * Returns the singleton instance of the Ink Virtual Machine, initialized with the specified namespace and list of file paths.
	 * <br/>
	 * <b>Application code should use the {@link #instance()} method instead of this one.</b>
	 * <br/>
	 * The supplied parameters have no effect if the singleton has already been initialized.
	 * @param defaultNamespace the default Ink namespace for the VM. May be <code>null</code>.
	 * @param paths a list of paths to the <code>dsls.ink</code> metadata files (those files are normally handled by Ink itself). May be <code>null</code>.
	 * @return the singleton instance of the Ink Virtual Machine, initialized with the specified namespace and list of file paths.
	 */
	public static VM instance(String defaultNamespace, String[] paths) {
		if (vm == null) {
			synchronized (InkVM.class) {
				if (vm == null) {
					vm = new InkVM(defaultNamespace, paths);
				}
			}
		}
		return vm;
	}

	private InkVM(String defaultNamespace, String[] paths) {
		VMMain.start(defaultNamespace, paths);
	}

	@Override
	public Context getContext() {
		return VMMain.getDefaultFactory().getAppContext();
	}

	@Override
	public DslFactory getFactory() {
		return VMMain.getDefaultFactory();
	}

	/**
	 * Shuts down the Ink Virtual Machine and clears all its resources.
	 * It's not necessary to explicitly shut down the Ink VM; it will shut down normally upon JVM exit.
	 */
	public static void destroy() {
		vm = null;
		VMMain.stop();
		ModelInfoFactory.getInstance().destroy();
	}

	/**
	 * Returns the default {@link InstanceFactory} of the Ink VM.
	 * @return the default {@link InstanceFactory} of the Ink VM.
	 */
	public static InstanceFactory getInstanceFactory() {
		return VMMain.getInstanceFactory();
	}

	@Override
	public DslFactory getOwnerFactory(File f) {
		DslFactory result = null;
		for (DslFactory fac : VMMain.getAllFactories()) {
			if (fac.containsFile(f)) {
				result = fac;
				break;
			}
		}
		return result;
	}

	@Override
	public void introduceNewDSl(String path) throws InkException {
		VMMain.introduceNewDsl(path);
	}

	@Override
	public DslFactory getFactory(String namespace) {
		return VMMain.getFactory(namespace);
	}

	@Override
	public List<InkErrorDetails> collectErrors() {
		List<InkErrorDetails> result = new ArrayList<InkErrorDetails>();
		for (DslFactory factory : VMMain.getAllFactories()) {
			result.addAll(factory.collectErrors());
		}
		return result;
	}

	@Override
	public List<InkErrorDetails> collectErrors(String namespace) {
		DslFactory factory = VMMain.getFactory(namespace);
		return factory.collectErrors();
	}

	@Override
	public void reloadDSL(String namespace) {
		DslFactory factory = VMMain.getFactory(namespace);
		if (factory != null) {
			factory.reload();
		}
	}

}
