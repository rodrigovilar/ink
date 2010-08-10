package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.property.AttributeState;
import org.ink.core.vm.types.EnumType;


/**
 * @author Lior Schachter
 */
public interface EnumAttributeState extends AttributeState{
	
	public static final byte p_default_value = 7;
	public static final byte p_final_value = 8;
	
	public String getDefaultValue();
	public void setDefaultValue(String value);
	
	public String getFinalValue();
	public void setFinalValue(String value);

	
	@Override
	public EnumType getType();
	
	public class Data extends AttributeState.Data implements EnumAttributeState{
		
		@Override
		public EnumType getType() {
			return (EnumType)getValue(p_type);
		}

		@Override
		public String getDefaultValue() {
			return (String)getValue(p_default_value);
		}

		@Override
		public void setDefaultValue(String value) {
			setValue(p_default_value, value);
		}

		@Override
		public String getFinalValue() {
			return (String)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(String value) {
			setValue(p_final_value, value);
		}
		
	}
}
