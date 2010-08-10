package org.ink.core.vm.factory;

import java.util.ArrayList;
import java.util.List;

import org.ink.core.vm.traits.TraitImpl;

/**
 * @author Lior Schachter
 */
public class DslFactoryEventDispatcherImpl<S extends DslFactoryEventDispatcherState> extends TraitImpl<S> implements DslFactoryEventDispatcher{

	private List<DslFactoryEventListener> listeners = new ArrayList<DslFactoryEventListener>();
	
	@Override
	public void addListener(DslFactoryEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void publishEvent(DslFactoryEvent event) {
		for(DslFactoryEventListener listener : listeners){
			listener.handleEvent(event);
		}
	}
	
}
