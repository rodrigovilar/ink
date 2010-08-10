package org.ink.core.vm.lang.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=ValuePropertyMirrorState.class, constraintsClass=PropertyConstraintsState.class)
public interface ValuePropertyState extends PropertyState{
	
	public static final byte p_value_calculator = 6;
	
	public PropertyValueCalculator getValueCalculator();
	public void setValueCalculator(PropertyValueCalculatorState value);

	public class Data extends PropertyState.Data implements ValuePropertyState{
		
		@Override
		public PropertyValueCalculator getValueCalculator() {
			return (PropertyValueCalculator)getValue(p_value_calculator);
		}

		@Override
		public void setValueCalculator(PropertyValueCalculatorState value) {
			setValue(p_value_calculator, value);
		}

	}
}
