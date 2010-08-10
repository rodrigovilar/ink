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
		finalValuesLocation={PropertyState.p_type}, finalValues={"ink.core:Integer"},
		validatorsKeys={"numeric_attribute_validator"},
		validatorsClasses={NumericAttributeValidatorState.class})
@CorePropertySpec(keys={"numeric_value_validator"}, validatorsClasses={NumericAttributeValueValidatorState.class})
public interface IntegerAttributeState extends NumericAttributeState{

	public Integer getDefaultValue();
	public void setDefaultValue(Integer value);
	
	public Integer getFinalValue();
	public void setFinalValue(Integer value);
	
	public Integer getMinValue();
	public void setMinValue(Integer value);
	
	public Integer getMaxValue();
	public void setMaxValue(Integer value);
	
	public class Data extends NumericAttributeState.Data implements IntegerAttributeState{

		@Override
		public Integer getDefaultValue() {
			return (Integer)getValue(p_default_value);
		}
		
		@Override
		public void setDefaultValue(Integer value){
			setValue(p_default_value, value);
		}
		
		@Override
		public Integer getMinValue() {
			return (Integer)getValue(p_min_value);
		}
		
		public void setMinValue(Integer value){
			setValue(p_min_value, value);
		}
		
		@Override
		public Integer getMaxValue() {
			return (Integer)getValue(p_max_value);
		}
		
		public void setMaxValue(Integer value){
			setValue(p_max_value, value);
		}

		@Override
		public Integer getFinalValue() {
			return (Integer)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(Integer value) {
			setValue(p_final_value, value);
		}
	}
}
