package org.ink.core.vm.factory;

import org.ink.core.vm.traits.TraitImpl;

/**
 * @author Lior Schachter
 */
public abstract class DslFactoryEventListenerTraitImpl<S extends DslFactoryEventListenerTraitState> extends TraitImpl<S> implements DslFactoryEventListenerTrait{

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		DslFactoryEventDispatcher dispatcher = getContext().getFactory().asTrait(DslFactoryState.t_event_dispatcher);
		dispatcher.addListener(this);
	}

}
