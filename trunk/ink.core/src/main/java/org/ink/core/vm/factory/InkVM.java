package org.ink.core.vm.factory;

import java.io.File;



/**
 * @author Lior Schachter
 */
public class InkVM implements VM {

	private static VM vm = null;

	public static VM instance(){
		return instance(null, null);
	}


	public static VM instance(String defaultNamespace, String[] paths){
		if(vm==null){
			synchronized (InkVM.class) {
				if(vm==null){
					vm = new InkVM(defaultNamespace, paths);
				}
			}
		}
		return vm;
	}

	private InkVM(){
	}

	private InkVM(String defaultNamespace, String[] paths){
		VMMain.start(defaultNamespace, paths);
	}

	@Override
	public Context getContext(){
		return VMMain.getDefaultFactory().getAppContext();
	}

	@Override
	public DslFactory getFactory(){
		return VMMain.getDefaultFactory();
	}

	@Override
	public void destroy() {
		VMMain.stop();
	}

	public static InstanceFactory getInstanceFactory() {
		return VMMain.getInstanceFactory();
	}

	@Override
	public DslFactory getOwnerFactory(File f){
		DslFactory result = null;
		for(DslFactory fac : VMMain.getAllFactories()){
			if(fac.containsFile(f)){
				result = fac;
				break;
			}
		}
		return result;
	}

	@Override
	public DslFactory getFactory(String namespace){
		return VMMain.getFactory(namespace);
	}


}
