package org.ink.core.vm.factory;

import org.ink.core.vm.traits.Trait;

/**
 * @author Lior Schachter
 */
public interface DslFactoryEventDispatcher extends Trait{

	public void addListener(DslFactoryEventListener listener);
	public void publishEvent(DslFactoryEvent event);
}
