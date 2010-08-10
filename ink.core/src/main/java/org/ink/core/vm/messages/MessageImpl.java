package org.ink.core.vm.messages;

import java.text.MessageFormat;

import org.ink.core.vm.lang.InkObjectImpl;


/**
 * @author Lior Schachter
*/
public class MessageImpl<S extends MessageState> extends InkObjectImpl<S> implements Message{

	@Override
	public String getFormattedMessage(Object... args) {
		return MessageFormat.format(getState().getText(), args);
	}
	
}
