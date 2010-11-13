package org.ink.core.vm.factory;



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
			vm = new InkVM(defaultNamespace, paths);
		}
		return vm;
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

	
}