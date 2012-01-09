package org.ink.core.vm.constraints;

import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.traits.Trait;

/**
 * @author Lior Schachter
 */
public interface Constraints extends Trait {

	public boolean validateTarget(Mirror stateSuper, ValidationContext context, SystemState systemState);
}
