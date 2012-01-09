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
		case Always:
			return true;
		default:
			switch (systemState) {
			case Design_Time:
				return whenToValidate == ActivationMode.Design_Time;
			default:
				return whenToValidate == ActivationMode.Run_Time;
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
