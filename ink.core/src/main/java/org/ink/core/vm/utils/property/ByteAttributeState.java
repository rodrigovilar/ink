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
		finalValuesLocation={PropertyState.p_type}, finalValues={"ink.core:Byte"},
		validatorsKeys={"numeric_attribute_validator"},
		validatorsClasses={NumericAttributeValidatorState.class})
@CorePropertySpec(keys={"numeric_value_validator"}, validatorsClasses={NumericAttributeValueValidatorState.class})
public interface ByteAttributeState extends NumericAttributeState{

	public Byte getDefaultValue();
	public void setDefaultValue(Byte value);
	
	public Byte getFinalValue();
	public void setFinalValue(Byte value);
	
	public Byte getMinValue();
	public void setMinValue(Byte value);
	
	public Byte getMaxValue();
	public void setMaxValue(Byte value);
	
	public class Data extends NumericAttributeState.Data implements ByteAttributeState{

		@Override
		public Byte getDefaultValue() {
			return (Byte)getValue(p_default_value);
		}
		
		@Override
		public void setDefaultValue(Byte value){
			setValue(p_default_value, value);
		}
		
		@Override
		public Byte getMinValue() {
			return (Byte)getValue(p_min_value);
		}
		
		public void setMinValue(Byte value){
			setValue(p_min_value, value);
		}
		
		@Override
		public Byte getMaxValue() {
			return (Byte)getValue(p_max_value);
		}
		
		public void setMaxValue(Byte value){
			setValue(p_max_value, value);
		}

		@Override
		public Byte getFinalValue() {
			return (Byte)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(Byte value) {
			setValue(p_final_value, value);
		}
	}
}
