package org.ink.core.vm.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ink.core.vm.exceptions.InkException;
import org.ink.core.vm.modelinfo.ModelInfoFactory;

/**
 * @author Lior Schachter
 */
public class InkVM implements VM {

	private static VM vm = null;

	public static VM instance() {
		return instance(null, null);
	}

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

	private InkVM() {
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

	public static void destroy() {
		vm = null;
		VMMain.stop();
		ModelInfoFactory.getInstance().destroy();
	}

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
