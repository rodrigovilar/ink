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
		finalValuesLocation={PropertyState.p_type}, finalValues={"ink.core:Float"},
		validatorsKeys={"numeric_attribute_validator"},
		validatorsClasses={NumericAttributeValidatorState.class})
@CorePropertySpec(keys={"numeric_value_validator"}, validatorsClasses={NumericAttributeValueValidatorState.class})
public interface FloatAttributeState extends NumericAttributeState{

	public Float getDefaultValue();
	public void setDefaultValue(Float value);
	
	public Float getFinalValue();
	public void setFinalValue(Float value);
	
	public Float getMinValue();
	public void setMinValue(Float value);
	
	public Float getMaxValue();
	public void setMaxValue(Float value);
	
	public class Data extends NumericAttributeState.Data implements FloatAttributeState{

		@Override
		public Float getDefaultValue() {
			return (Float)getValue(p_default_value);
		}
		
		@Override
		public void setDefaultValue(Float value){
			setValue(p_default_value, value);
		}
		
		@Override
		public Float getMinValue() {
			return (Float)getValue(p_min_value);
		}
		
		public void setMinValue(Float value){
			setValue(p_min_value, value);
		}
		
		@Override
		public Float getMaxValue() {
			return (Float)getValue(p_max_value);
		}
		
		public void setMaxValue(Float value){
			setValue(p_max_value, value);
		}

		@Override
		public Float getFinalValue() {
			return (Float)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(Float value) {
			setValue(p_final_value, value);
		}
	}
}
