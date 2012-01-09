package org.ink.core.vm.constraints;

import java.util.Map;

import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreMapField;
import org.ink.core.vm.traits.TraitState;

/**
 * @author Lior Schachter
 */
public interface ConstraintsState extends TraitState {

	@CoreField(mandatory = true)
	public static final byte p_generic_constraints = 1;
	@CoreMapField(keyName = "name", valueName = "validator", kind = org.ink.core.vm.lang.internal.annotations.CoreMapField.Kind.key_value)
	public static final byte p_validators = 2;

	public InstanceValidator getGenericConstraints();

	public void setGenericConstraints(InstanceValidatorState value);

	public Map<String, InstanceValidator> getValidators();

	public void setValidators(Map<String, ? extends InstanceValidatorState> value);

	public class Data extends TraitState.Data implements ConstraintsState {

		@Override
		public InstanceValidator getGenericConstraints() {
			return (InstanceValidator) getValue(p_generic_constraints);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, InstanceValidator> getValidators() {
			return (Map<String, InstanceValidator>) getValue(p_validators);
		}

		@Override
		public void setGenericConstraints(InstanceValidatorState value) {
			setValue(p_generic_constraints, value);
		}

		@Override
		public void setValidators(Map<String, ? extends InstanceValidatorState> value) {
			setValue(p_validators, value);
		}

	}

}
