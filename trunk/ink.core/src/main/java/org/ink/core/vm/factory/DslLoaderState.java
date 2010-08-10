package org.ink.core.vm.factory;

import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface DslLoaderState extends InkObjectState{
	
	public class Data extends InkObjectState.Data implements DslLoaderState{
	}

}
