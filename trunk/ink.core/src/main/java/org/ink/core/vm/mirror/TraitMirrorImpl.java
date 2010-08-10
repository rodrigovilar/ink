package org.ink.core.vm.mirror;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitState;

/**
 * @author Lior Schachter
 */
public class TraitMirrorImpl<S extends TraitMirrorState> extends MirrorImpl<S> implements TraitMirror{

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Trait> T adapt(InkObjectState state) {
		return (T) getContext().getFactory().newBehaviorInstance((TraitState)getTargetState(), state, false, true);
	}

	
}
