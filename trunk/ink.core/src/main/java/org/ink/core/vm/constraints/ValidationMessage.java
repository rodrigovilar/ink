package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.messages.Message;

/**
 * @author Lior Schachter
 */
public interface ValidationMessage extends InkObject{

	public void fill(InkObjectState erroneousObject, Message msg, Severity severity, ResourceType resourceType, Object... args);
	public String getFormattedMessage();
	public String getErrorPath();
	public Severity getSeverity();
	public ResourceType getResourceType();
	public Message getMessageTemplate();

}
