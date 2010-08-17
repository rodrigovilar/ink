package org.ink.core.vm.factory;


/**
 * @author Lior Schachter
 */
public class InkVM implements VM {

	private static VM vm = new InkVM();
	
	public static VM instance(){
		return vm;
	}
	
	private InkVM(){
		VMMain.start();
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
	public Context getContext(String namespace) {
		return VMMain.getFactory(namespace).getAppContext();
	}
	
	@Override
	public DslFactory getFactory(String namespace) {
		return VMMain.getFactory(namespace);
	}
	
	
	@Override
	public void destroy() {
		VMMain.stop();
	}

	
}
