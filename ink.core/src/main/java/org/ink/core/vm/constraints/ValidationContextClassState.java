package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.internal.annotations.CoreField;

/**
 * @author Lior Schachter
 */
public interface ValidationContextClassState extends InkClassState{
	
	@CoreField(mandatory=true, defaultValue="ink.core:ValidationMessage")
	public static final byte p_error_message_class = p_personality + 1;
	
	public InkClass getErrorMessageClass();
	public void setErrorMessageClass(ValidationMessageClassState value);
	
	public class Data extends InkClassState.Data implements ValidationContextClassState{

		@Override
		public InkClass getErrorMessageClass() {
			return (InkClass)getValue(p_error_message_class);
		}

		@Override
		public void setErrorMessageClass(ValidationMessageClassState value) {
			setValue(p_error_message_class, value);
		}
		
	}

}
