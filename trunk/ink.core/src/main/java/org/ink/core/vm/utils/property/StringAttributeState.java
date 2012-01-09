package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CorePropertySpec;
import org.ink.core.vm.utils.property.constraints.StringAttributeValidatorState;
import org.ink.core.vm.utils.property.constraints.StringAttributeValueValidatorState;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass = PrimitiveAttributeMirrorState.class, constraintsClass = PropertyConstraintsState.class, finalValuesLocation = { PropertyState.p_type }, finalValues = { "ink.core:String" }, validatorsKeys = { "string_attribute_validator" }, validatorsClasses = { StringAttributeValidatorState.class })
@CorePropertySpec(keys = { "string_value_validator" }, validatorsClasses = { StringAttributeValueValidatorState.class })
public interface StringAttributeState extends PrimitiveAttributeState {

	@CoreField(mandatory = false)
	public static final byte p_default_value = 7;
	@CoreField(mandatory = false)
	public static final byte p_final_value = 8;
	@CoreField(mandatory = false)
	public static final byte p_min_length = 9;
	@CoreField(mandatory = false)
	public static final byte p_max_length = 10;
	@CoreField(mandatory = false)
	public static final byte p_reg_exp = 11;

	public String getDefaultValue();

	public void setDefaultValue(String value);

	public String getFinalValue();

	public void setFinalValue(String value);

	public Integer getMinLength();

	public void setMinLength(Integer value);

	public Integer getMaxLength();

	public void setMaxLength(Integer value);

	public String getRegExp();

	public void setRegExp(String value);

	public class Data extends PrimitiveAttributeState.Data implements StringAttributeState {

		@Override
		public Integer getMaxLength() {
			return (Integer) getValue(p_max_length);
		}

		@Override
		public Integer getMinLength() {
			return (Integer) getValue(p_min_length);
		}

		@Override
		public void setMaxLength(Integer value) {
			setValue(p_max_length, value);
		}

		@Override
		public void setMinLength(Integer value) {
			setValue(p_min_length, value);
		}

		@Override
		public String getRegExp() {
			return (String) getValue(p_reg_exp);
		}

		@Override
		public void setRegExp(String value) {
			setValue(p_reg_exp, value);
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
