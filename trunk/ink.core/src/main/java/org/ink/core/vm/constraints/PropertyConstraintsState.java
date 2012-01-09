package org.ink.core.vm.constraints;

import java.util.Map;

import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreMapField;

/**
 * @author Lior Schachter
 */
public interface PropertyConstraintsState extends ConstraintsState {

	@CoreField(mandatory = true)
	public static final byte p_generic_property_value_constraints = 3;
	@CoreMapField(keyName = "key", valueName = "validator", kind = org.ink.core.vm.lang.internal.annotations.CoreMapField.Kind.key_value)
	public static final byte p_property_value_validators = 4;

	public PropertyValueValidator getGenericPropertyValueConstraints();

	public void setGenericPropertyValueConstraints(PropertyValueValidatorState value);

	public Map<String, PropertyValueValidator> getPropertyValueValidators();

	public void setPropertyValueValidators(Map<String, PropertyValueValidatorState> value);

	public class Data extends ConstraintsState.Data implements PropertyConstraintsState {

		@Override
		public PropertyValueValidator getGenericPropertyValueConstraints() {
			return (PropertyValueValidator) getValue(p_generic_property_value_constraints);
		}

		@Override
		public void setGenericPropertyValueConstraints(PropertyValueValidatorState value) {
			setValue(p_generic_property_value_constraints, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, PropertyValueValidator> getPropertyValueValidators() {
			return (Map<String, PropertyValueValidator>) getValue(p_property_value_validators);
		}

		@Override
		public void setPropertyValueValidators(Map<String, PropertyValueValidatorState> value) {
			setValue(p_property_value_validators, value);
		}

	}

}
