package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.PropertyValueValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 */
@ValidatorMessages(codes={"min.length.violation", "max.length.violation","reg.exp.violation"}, 
				   messages={"ink.core:string_min_length_violation", "ink.core:string_max_length_violation", "ink.core:string_reg_exp_violation"})
@CoreClassSpec(metaclass=ValidatorClassState.class, javaMapping=JavaMapping.State_Behavior)				   
public interface StringAttributeValueValidatorState extends PropertyValueValidatorState{
	
	public class Data extends PropertyValueValidatorState.Data implements StringAttributeValueValidatorState{
	}

}
