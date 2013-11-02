package org.ink.core.vm.lang.constraints;

import org.ink.core.vm.constraints.PropertyValueValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass = ValidatorClassState.class, javaMapping = JavaMapping.STATE_BEHAVIOR)
@ValidatorMessages(codes = { "field.required", "wrong.value.type" }, messages = { "ink.core:missing_field_data", "ink.core:wrong_value_type" })
public interface GenericPropertyValueValidatorState extends PropertyValueValidatorState {

	public class Data extends PropertyValueValidatorState.Data implements GenericPropertyValueValidatorState {
	}

}
