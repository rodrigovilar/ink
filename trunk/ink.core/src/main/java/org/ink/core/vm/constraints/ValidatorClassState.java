package org.ink.core.vm.constraints;

import java.util.Map;

import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreMapField;
import org.ink.core.vm.messages.Message;
import org.ink.core.vm.messages.MessageState;

/**
 * @author Lior Schachter
 */
public interface ValidatorClassState extends InkClassState{
	
	@CoreField(defaultValue="false")
	public static final byte p_abort_on_error = p_personality + 1;
	@CoreField(defaultValue="Always")
	public static final byte p_active_at = p_abort_on_error + 1;
	@CoreField(mandatory=false)
	public static final byte p_default_message = p_active_at +1;
	@CoreMapField(keyName="code", valueName="message")
	public static final byte p_specific_messages = p_default_message + 1;
	
	
	public Boolean getAbortOnError();
	public void setAbortOnError(Boolean value);
	
	public ActivationMode getActiveAt();
	public void setActiveAt(ActivationMode value);
	
	public Message getDefaultMessage();
	public void setDefaultMessage(MessageState value);
	
	public Map<String, Message> getSpecificMessages();
	public void setSpecificMessages(Map<String, MessageState> value);
	
	public class Data extends InkClassState.Data implements ValidatorClassState{

		@Override
		public Boolean getAbortOnError() {
			return (Boolean) getValue(p_abort_on_error);
		}

		@Override
		public void setAbortOnError(Boolean value) {
			setValue(p_abort_on_error, value);
		}

		@Override
		public ActivationMode getActiveAt() {
			return (ActivationMode) getValue(p_active_at);
		}

		@Override
		public void setActiveAt(ActivationMode value) {
			setValue(p_active_at, value);
		}

		@Override
		public Message getDefaultMessage() {
			return (Message)getValue(p_default_message);
		}

		@Override
		public void setDefaultMessage(MessageState value) {
			setValue(p_default_message, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Message> getSpecificMessages() {
			return (Map<String, Message>) getValue(p_specific_messages);
		}

		@Override
		public void setSpecificMessages(Map<String, MessageState> value) {
			setValue(p_specific_messages, value);
		}
		
	}

}
