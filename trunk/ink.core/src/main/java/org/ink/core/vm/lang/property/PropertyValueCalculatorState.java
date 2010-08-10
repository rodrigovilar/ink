package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface PropertyValueCalculatorState extends InkObjectState{

	public static final byte p_has_static_value = 0;

	public Boolean getHasStaticValue();
	public void setHasStaticValue(Boolean value);
	
	public class Data extends InkObjectState.Data implements PropertyValueCalculatorState{

		@Override
		public Boolean getHasStaticValue() {
			return (Boolean)getValue(p_has_static_value);
		}

		@Override
		public void setHasStaticValue(Boolean value) {
			setValue(p_has_static_value, value);
		}
		
	}
	
}
