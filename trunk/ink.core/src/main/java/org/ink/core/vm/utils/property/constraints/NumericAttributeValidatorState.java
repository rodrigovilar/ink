package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.InstanceValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 */
@ValidatorMessages(codes = { "min.value.violation", "values.comparison.violation", "max.value.violation" }, messages = { "ink.core:min_value_violation", "ink.core:values_comparison_violation", "ink.core:max_value_violation" })
@CoreClassSpec(metaclass = ValidatorClassState.class, javaMapping = JavaMapping.STATE_BEHAVIOR)
public interface NumericAttributeValidatorState extends InstanceValidatorState {

	public class Data extends InstanceValidatorState.Data implements NumericAttributeValidatorState {
	}

}
