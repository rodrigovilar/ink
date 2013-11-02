package org.ink.core.vm.constraints;

import java.util.Map;

import org.ink.core.vm.lang.InkClassImpl;
import org.ink.core.vm.messages.Message;

/**
 * @author Lior Schachter
 */
public class ValidatorClassImpl<S extends ValidatorClassState> extends InkClassImpl<S> implements ValidatorClass {

	@Override
	public boolean shouldValidate(SystemState systemState) {
		ActivationMode whenToValidate = getState().getActiveAt();
		switch (whenToValidate) {
		case ALWAYS:
			return true;
		default:
			switch (systemState) {
			case DESIGN_TIME:
				return whenToValidate == ActivationMode.DESIGN_TIME;
			default:
				return whenToValidate == ActivationMode.RUN_TIME;
			}
		}
	}

	@Override
	public Message getDefaultMessage() {
		return getState().getDefaultMessage();
	}

	@Override
	public Message getMessage(String code) {
		Map<String, Message> messages = getState().getSpecificMessages();
		if (messages != null) {
			return messages.get(code);
		}
		return null;
	}

	@Override
	public boolean abortOnError() {
		return getState().getAbortOnError();
	}

	@Override
	public Message getErrorMessage(String errorCode) {
		// TODO Auto-generated method stub
		return null;
	}

}
