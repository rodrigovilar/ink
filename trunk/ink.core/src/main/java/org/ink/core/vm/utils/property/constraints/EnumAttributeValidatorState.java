package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.InstanceValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 */
@ValidatorMessages(codes = { "enum.illegal.value" }, messages = { "ink.core:enum_illegal_value" })
@CoreClassSpec(metaclass = ValidatorClassState.class, javaMapping = JavaMapping.State_Behavior)
public interface EnumAttributeValidatorState extends InstanceValidatorState {

	public class Data extends InstanceValidatorState.Data implements EnumAttributeValidatorState {
	}

}
