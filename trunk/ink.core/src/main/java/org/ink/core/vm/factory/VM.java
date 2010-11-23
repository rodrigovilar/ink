package org.ink.core.vm.factory;

import java.io.File;


/**
 * @author Lior Schachter
 */
public interface VM {
	public DslFactory getFactory(String namespace);
	public DslFactory getOwnerFactory(File f);
	public Context getContext();
	public DslFactory getFactory();
	public void destroy();
	
}
