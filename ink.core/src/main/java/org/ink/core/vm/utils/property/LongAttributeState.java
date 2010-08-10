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
		finalValuesLocation={PropertyState.p_type}, finalValues={"ink.core:Long"},
		validatorsKeys={"numeric_attribute_validator"},
		validatorsClasses={NumericAttributeValidatorState.class})
@CorePropertySpec(keys={"numeric_value_validator"}, validatorsClasses={NumericAttributeValueValidatorState.class})
public interface LongAttributeState extends NumericAttributeState{

	public Long getDefaultValue();
	public void setDefaultValue(Long value);
	
	public Long getFinalValue();
	public void setFinalValue(Long value);
	
	public Long getMinValue();
	public void setMinValue(Long value);
	
	public Long getMaxValue();
	public void setMaxValue(Long value);
	
	public class Data extends NumericAttributeState.Data implements LongAttributeState{

		@Override
		public Long getDefaultValue() {
			return (Long)getValue(p_default_value);
		}
		
		@Override
		public void setDefaultValue(Long value){
			setValue(p_default_value, value);
		}
		
		@Override
		public Long getMinValue() {
			return (Long)getValue(p_min_value);
		}
		
		public void setMinValue(Long value){
			setValue(p_min_value, value);
		}
		
		@Override
		public Long getMaxValue() {
			return (Long)getValue(p_max_value);
		}
		
		public void setMaxValue(Long value){
			setValue(p_max_value, value);
		}

		@Override
		public Long getFinalValue() {
			return (Long)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(Long value) {
			setValue(p_final_value, value);
		}
	}
}
