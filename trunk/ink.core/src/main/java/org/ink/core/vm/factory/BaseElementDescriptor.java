package org.ink.core.vm.factory;

import java.util.List;

import org.ink.core.vm.constraints.ValidationMessage;
import org.ink.core.vm.serialization.ParseError;

/**
 * @author Lior Schachter
 */
public abstract class BaseElementDescriptor<D> implements ElementDescriptor<D>{

	public List<ParseError> parsingErrors = null;
	public List<ValidationMessage> validationErrorMessages = null;


	@Override
	public List<ParseError> getParsingErrors() {
		return parsingErrors;
	}
	@Override
	public void setParsingErrors(List<ParseError> parsingErrors) {
		this.parsingErrors = parsingErrors;
	}
	@Override
	public List<ValidationMessage> getValidationErrorMessages() {
		return validationErrorMessages;
	}
	@Override
	public void setValidationErrorMessages(
			List<ValidationMessage> validationErrorMessages) {
		this.validationErrorMessages = validationErrorMessages;
	}

	@Override
	public void clearErrors() {
		parsingErrors = null;
		validationErrorMessages = null;
	}

	@Override
	public boolean containsErrors() {
		if(parsingErrors!=null){
			return !parsingErrors.isEmpty();
		}else if(validationErrorMessages!=null){
			return !validationErrorMessages.isEmpty();
		}
		return false;
	}


}
