package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public interface InstanceValidator extends Validator{
	
	public void validate(InkObjectState target, Mirror targetSuper, ValidationContext context, SystemState systemState);
	
}
