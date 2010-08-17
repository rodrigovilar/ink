package org.ink.core.vm.factory;


/**
 * @author Lior Schachter
 */
public interface VM {
	
	public Context getContext(String namespace);
	public Context getContext();
	public DslFactory getFactory(String namespace);
	public DslFactory getFactory();
	public void destroy();
	
}
