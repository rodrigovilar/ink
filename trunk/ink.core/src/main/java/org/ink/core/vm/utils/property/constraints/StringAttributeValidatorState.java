package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.InstanceValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 */
@ValidatorMessages(codes={"min.value.violation", "values.comparison.violation", "min.length.violation", "max.length.violation","reg.exp.violation"}, 
		   messages={"ink.core:min_value_violation", "ink.core:values_comparison_violation", "ink.core:string_min_length_violation", "ink.core:string_max_length_violation", "ink.core:string_reg_exp_violation"})
@CoreClassSpec(metaclass=ValidatorClassState.class, javaMapping=JavaMapping.State_Behavior)
public interface StringAttributeValidatorState extends InstanceValidatorState{
	
	public class Data extends InstanceValidatorState.Data implements StringAttributeValidatorState{
	}

}