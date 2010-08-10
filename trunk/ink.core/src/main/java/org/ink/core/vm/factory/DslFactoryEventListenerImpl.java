package org.ink.core.vm.factory;

import org.ink.core.vm.traits.TraitImpl;

/**
 * @author Lior Schachter
 */
public abstract class DslFactoryEventListenerImpl<S extends DslFactoryEventListenerState> extends TraitImpl<S> implements DslFactoryEventListener{

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		DslFactoryEventDispatcher dispatcher = getContext().getFactory().asTrait(DslFactoryState.t_event_dispatcher);
		dispatcher.addListener(this);
	}
	
}
