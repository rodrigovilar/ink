package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkClass;

/**
 * @author Lior Schachter
 */
public interface ValidationContextClass extends InkClass {

	public <T extends ValidationMessage> T instantiateErrorMessage();

}
