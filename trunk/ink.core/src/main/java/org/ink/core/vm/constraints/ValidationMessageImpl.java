package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.messages.Message;

/**
 * @author Lior Schachter
 */
public class ValidationMessageImpl<S extends ValidationMessageState> extends InkObjectImpl<S> implements ValidationMessage {

	private InkObjectState o;
	private Message msg;
	private Severity severity;
	private ResourceType resourceType;
	private Object[] args;

	private String formattedMessage = null;

	@Override
	public String getErrorPath() {
		// TODO to be implemented once we have path feature
		return null;
	}

	@Override
	public final String getFormattedMessage() {
		if (formattedMessage == null) {
			formattedMessage = produceMessage(o, msg, severity, args);
		}
		return formattedMessage;
	}

	protected String produceMessage(InkObjectState erroneousObject, Message msg, Severity severity, Object... args) {
		return msg.getFormattedMessage(args);
	}

	@Override
	public void fill(InkObjectState erroneousObject, Message msg, Severity severity, ResourceType resourceType, Object... args) {
		this.o = erroneousObject;
		this.msg = msg;
		this.severity = severity;
		this.args = args;
		this.resourceType = resourceType;
	}

	@Override
	public Severity getSeverity() {
		return severity;
	}

	@Override
	public Message getMessageTemplate() {
		return msg;
	}

	@Override
	public ResourceType getResourceType() {
		return resourceType;
	}

}
