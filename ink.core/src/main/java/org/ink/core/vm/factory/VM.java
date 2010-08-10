package org.ink.core.vm.factory;


/**
 * @author Lior Schachter
 */
public interface VM {
	
	public Context getContext();
	public DslFactory getFactory();
	public void destroy();
	
}
