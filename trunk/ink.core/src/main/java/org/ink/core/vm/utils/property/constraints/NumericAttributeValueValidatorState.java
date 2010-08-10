package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.PropertyValueValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 */
@ValidatorMessages(codes={"min.value.violation", "max.value.violation"}, 
				   messages={"ink.core:min_value_violation", "ink.core:max_value_violation"})
@CoreClassSpec(metaclass=ValidatorClassState.class, javaMapping=JavaMapping.State_Behavior)				   
public interface NumericAttributeValueValidatorState extends PropertyValueValidatorState{
	
	public class Data extends PropertyValueValidatorState.Data implements NumericAttributeValueValidatorState{
	}

}
