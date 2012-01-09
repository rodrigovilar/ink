package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CorePropertySpec;
import org.ink.core.vm.lang.property.AttributeState;
import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorState;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.utils.property.constraints.EnumAttributeValidatorState;
import org.ink.core.vm.utils.property.constraints.EnumAttributeValueValidatorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass = ValuePropertyMirrorState.class, constraintsClass = PropertyConstraintsState.class, validatorsKeys = { "enum_attribute_validator" }, validatorsClasses = { EnumAttributeValidatorState.class })
@CorePropertySpec(keys = { "enum_value_validator" }, validatorsClasses = { EnumAttributeValueValidatorState.class })
public interface EnumAttributeState extends AttributeState {

	public static final byte p_default_value = 7;
	public static final byte p_final_value = 8;

	public String getDefaultValue();

	public void setDefaultValue(String value);

	public String getFinalValue();

	public void setFinalValue(String value);

	@Override
	public EnumType getType();

	public class Data extends AttributeState.Data implements EnumAttributeState {

		@Override
		public EnumType getType() {
			return (EnumType) getValue(p_type);
		}

		@Override
		public String getDefaultValue() {
			return (String) getValue(p_default_value);
		}

		@Override
		public void setDefaultValue(String value) {
			setValue(p_default_value, value);
		}

		@Override
		public String getFinalValue() {
			return (String) getValue(p_final_value);
		}

		@Override
		public void setFinalValue(String value) {
			setValue(p_final_value, value);
		}

	}
}
