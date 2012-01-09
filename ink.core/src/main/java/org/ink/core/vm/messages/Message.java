package org.ink.core.vm.messages;

import org.ink.core.vm.lang.InkObject;

/**
 * @author Lior Schachter
 */
public interface Message extends InkObject {
	public String getFormattedMessage(Object... args);
}
