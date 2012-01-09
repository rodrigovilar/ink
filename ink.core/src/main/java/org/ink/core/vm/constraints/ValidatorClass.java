package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.messages.Message;

/**
 * @author Lior Schachter
 */
public interface ValidatorClass extends InkClass {

	public boolean shouldValidate(SystemState systemState);

	public Message getDefaultMessage();

	public Message getMessage(String code);

	public Message getErrorMessage(String errorCode);

	public boolean abortOnError();
}
