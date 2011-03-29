package org.ink.core.vm.constraints;

import java.util.ArrayList;
import java.util.List;

import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.messages.Message;


/**
 * @author Lior Schachter
*/
public class ValidationContextImpl<S extends ValidatorClassState> extends InkObjectImpl<S> implements ValidationContext{

	private List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
	private boolean abort = false;

	@Override
	public boolean aborted(){
		return abort;
	}

	@Override
	public void addError(InkObjectState erroneousObject, Validator validator) {
		add(erroneousObject, validator, null, Severity.Error);
	}

	@Override
	public void addError(InkObjectState erroneousObject, Validator validator, String code) {
		add(erroneousObject, validator, code, Severity.Error,(Object[])null);
	}

	@Override
	public void addError(InkObjectState erroneousObject, Validator validator, Object... args) {
		add(erroneousObject, validator, null, Severity.Error, args);
	}

	@Override
	public void addError(InkObjectState erroneousObject, Validator validator, String code, Object... args) {
		add(erroneousObject, validator, code, Severity.Error, args);
	}

	@Override
	public void addWarning(InkObjectState erroneousObject, Validator validator) {
		add(erroneousObject, validator, null, Severity.Warning);
	}

	@Override
	public void addWarning(InkObjectState erroneousObject, Validator validator,
			Object... args) {
		add(erroneousObject, validator, null,Severity.Warning, args);
	}

	@Override
	public void addWarning(InkObjectState erroneousObject, Validator validator, String code) {
		add(erroneousObject, validator, null, Severity.Warning);
	}

	@Override
	public void addWarning(InkObjectState erroneousObject, Validator validator, String code,
			Object... args) {
		add(erroneousObject, validator, code, Severity.Warning, args);
	}

	private void add(InkObjectState erroneousObject, Validator validator, String code,
			Severity severity, Object... args){
		ValidatorClass validatorCls = validator.getMeta();
		Message msg = null;
		if(code==null){
			msg = validatorCls.getDefaultMessage();
		}else{
			msg = validatorCls.getMessage(code);
			if(msg == null){
				msg = validatorCls.getDefaultMessage();
			}
		}
		if(msg==null){
			//TODO - should add generic error message
		}
		ValidationMessage validatorMsg = ((ValidationContextClass)getMeta()).instantiateErrorMessage();
		validatorMsg.fill(erroneousObject, msg, severity, args);
		abort |= (validatorCls.abortOnError() && severity.ordinal()>=Severity.Error.ordinal());
		messages.add(validatorMsg);
	}

	@Override
	public List<ValidationMessage> getMessages(){
		return messages;
	}

	@Override
	public boolean containsMessage(){
		return !messages.isEmpty();
	}

	@Override
	public boolean containsMessage(Severity severity){
		if(messages.isEmpty()){
			return false;
		}
		for(ValidationMessage msg : messages){
			if(severity.ordinal()<=msg.getSeverity().ordinal()){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsError(){
		return containsMessage(Severity.Error);
	}

	@Override
	public void reset() {
		messages = new ArrayList<ValidationMessage>();
		abort = false;
	}

	@Override
	public void logMessages() {
		logMessages(Severity.Warning);
	}

	@Override
	public void logMessages(Severity severity) {
		for(ValidationMessage msg : messages){
			if(severity.ordinal()<=msg.getSeverity().ordinal()){
				//TODO - change to logger
				System.out.println(msg.getFormattedMessage());
			}
		}
	}


}
