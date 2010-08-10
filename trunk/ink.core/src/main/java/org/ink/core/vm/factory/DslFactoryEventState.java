package org.ink.core.vm.factory;

import org.ink.core.vm.traits.TraitState;

/**
 * @author Lior Schachter
 */
public interface DslFactoryEventState extends TraitState{
	
	public class Data extends TraitState.Data implements DslFactoryEventState{
		
	}

}
