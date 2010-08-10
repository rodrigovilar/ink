package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkObject;

/**
 * @author Lior Schachter
 */
public interface Validator extends InkObject{
	
	public boolean shouldValidate(SystemState systemState);
	
}
