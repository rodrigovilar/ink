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
@CoreClassSpec(mirrorClass = PrimitiveAttributeMirrorState.class, constraintsClass = PropertyConstraintsState.class, finalValuesLocation = { PropertyState.p_type }, finalValues = { "ink.core:Short" }, validatorsKeys = { "numeric_attribute_validator" }, validatorsClasses = { NumericAttributeValidatorState.class })
@CorePropertySpec(keys = { "numeric_value_validator" }, validatorsClasses = { NumericAttributeValueValidatorState.class })
public interface ShortAttributeState extends NumericAttributeState {

	public Short getDefaultValue();

	public void setDefaultValue(Short value);

	public Short getFinalValue();

	public void setFinalValue(Short value);

	public Short getMinValue();

	public void setMinValue(Short value);

	public Short getMaxValue();

	public void setMaxValue(Short value);

	public class Data extends NumericAttributeState.Data implements ShortAttributeState {

		@Override
		public Short getDefaultValue() {
			return (Short) getValue(p_default_value);
		}

		@Override
		public void setDefaultValue(Short value) {
			setValue(p_default_value, value);
		}

		@Override
		public Short getMinValue() {
			return (Short) getValue(p_min_value);
		}

		public void setMinValue(Short value) {
			setValue(p_min_value, value);
		}

		@Override
		public Short getMaxValue() {
			return (Short) getValue(p_max_value);
		}

		public void setMaxValue(Short value) {
			setValue(p_max_value, value);
		}

		@Override
		public Short getFinalValue() {
			return (Short) getValue(p_final_value);
		}

		@Override
		public void setFinalValue(Short value) {
			setValue(p_final_value, value);
		}
	}
}
