package org.ink.core.vm.constraints;

import java.util.List;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface ValidationContext extends InkObject{
	
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
	
	public void reset();
	
}
