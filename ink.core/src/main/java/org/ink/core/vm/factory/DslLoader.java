package org.ink.core.vm.factory;

import org.ink.core.vm.exceptions.ObjectLoadingException;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface DslLoader extends InkObject {
	
	public void scan(DslFactory ownerFactory);
	public InkObjectState getObject(String id) throws ObjectLoadingException;

}
