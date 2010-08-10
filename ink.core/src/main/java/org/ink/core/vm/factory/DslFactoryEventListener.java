package org.ink.core.vm.factory;

import org.ink.core.vm.traits.Trait;

/**
 * @author Lior Schachter
 */
public interface DslFactoryEventListener extends Trait{
	
	public void handleEvent(DslFactoryEvent event);
}
