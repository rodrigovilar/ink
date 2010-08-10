package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectImpl;

/**
 * @author Lior Schachter
 */
public abstract class TargetLocatorImpl<S extends TargetLocatorState> extends InkObjectImpl<S> implements TargetLocator{
	
	@Override
	public boolean isAcceptable(InkClass cls) {
		return match().contains(cls);
	}
	
}
