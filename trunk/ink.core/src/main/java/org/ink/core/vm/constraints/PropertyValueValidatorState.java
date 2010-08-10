package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass=ValidatorClassState.class, javaMapping=JavaMapping.State_Interface)
public interface PropertyValueValidatorState extends ValidatorState{
	
	public class Data extends ValidatorState.Data implements PropertyValueValidatorState{
	}

}
