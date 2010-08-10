package org.ink.core.vm.lang.constraints;

import org.ink.core.vm.constraints.InstanceValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass=ValidatorClassState.class, javaMapping=JavaMapping.State_Behavior)
@ValidatorMessages(codes={"refinement.violation",
						  "component.type.violation"}, 
				   messages={"ink.core:refinement_violation",
							 "ink.core:component_type_violation"})
public interface GenericInstanceValidatorState extends InstanceValidatorState{
	
	public class Data extends InstanceValidatorState.Data implements GenericInstanceValidatorState{
	}

}
