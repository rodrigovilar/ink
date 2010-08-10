package org.ink.core.vm.mirror;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.traits.Trait;


/**
 * @author Lior Schachter
 */
public interface TraitMirror extends Mirror{
	public <T extends Trait> T adapt(InkObjectState state);
}
