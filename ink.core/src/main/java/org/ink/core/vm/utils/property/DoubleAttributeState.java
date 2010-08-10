package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CorePropertySpec;
import org.ink.core.vm.utils.property.constraints.NumericAttributeValidatorState;
import org.ink.core.vm.utils.property.constraints.NumericAttributeValueValidatorState;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=PrimitiveAttributeMirrorState.class, constraintsClass=PropertyConstraintsState.class,
		finalValuesLocation={PropertyState.p_type}, finalValues={"ink.core:Double"},
		validatorsKeys={"numeric_attribute_validator"},
		validatorsClasses={NumericAttributeValidatorState.class})
@CorePropertySpec(keys={"numeric_value_validator"}, validatorsClasses={NumericAttributeValueValidatorState.class})
public interface DoubleAttributeState extends NumericAttributeState{

	public Double getDefaultValue();
	public void setDefaultValue(Double value);
	
	public Double getFinalValue();
	public void setFinalValue(Double value);
	
	public Double getMinValue();
	public void setMinValue(Double value);
	
	public Double getMaxValue();
	public void setMaxValue(Double value);
	
	public class Data extends NumericAttributeState.Data implements DoubleAttributeState{

		@Override
		public Double getDefaultValue() {
			return (Double)getValue(p_default_value);
		}
		
		@Override
		public void setDefaultValue(Double value){
			setValue(p_default_value, value);
		}
		
		@Override
		public Double getMinValue() {
			return (Double)getValue(p_min_value);
		}
		
		public void setMinValue(Double value){
			setValue(p_min_value, value);
		}
		
		@Override
		public Double getMaxValue() {
			return (Double)getValue(p_max_value);
		}
		
		public void setMaxValue(Double value){
			setValue(p_max_value, value);
		}

		@Override
		public Double getFinalValue() {
			return (Double)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(Double value) {
			setValue(p_final_value, value);
		}
	}
}
