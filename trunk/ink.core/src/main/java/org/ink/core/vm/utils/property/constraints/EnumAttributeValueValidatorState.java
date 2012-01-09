package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.PropertyValueValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 */
@ValidatorMessages(codes = { "enum.illegal.value" }, messages = { "ink.core:enum_illegal_value" })
@CoreClassSpec(metaclass = ValidatorClassState.class, javaMapping = JavaMapping.State_Behavior)
public interface EnumAttributeValueValidatorState extends PropertyValueValidatorState {

	public class Data extends PropertyValueValidatorState.Data implements EnumAttributeValueValidatorState {
	}

}
