package org.ink.core.vm.constraints;

import java.util.List;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.messages.Message;

/**
 * @author Lior Schachter
 */
public interface ValidationContext extends InkObject{

	public void add(InkObjectState erroneousObject, Message msg, Severity severity, boolean abortOnError, Object... args);
	public void add(InkObjectState erroneousObject, Message msg, Severity severity,ResourceType resourceType, boolean abortOnError, Object... args);

	public void addError(InkObjectState erroneousObject, Validator validator);
	public void addError(InkObjectState erroneousObject, Validator validator, String code);
	public void addError(InkObjectState erroneousObject, Validator validator, Object... args);
	public void addError(InkObjectState erroneousObject, Validator validator, String code, Object... args);

	public void addWarning(InkObjectState erroneousObject, Validator validator);
	public void addWarning(InkObjectState erroneousObject, Validator validator, Object... args);
	public void addWarning(InkObjectState erroneousObject, Validator validator, String code);
	public void addWarning(InkObjectState erroneousObject, Validator validator, String code, Object... args);

	public List<ValidationMessage> getMessages();
	public boolean containsMessage();
	public boolean containsMessage(Severity severity);

	public boolean aborted();
	public boolean containsError();

	public void logMessages();
	public void logMessages(Severity severity);
	public void reset();

}
