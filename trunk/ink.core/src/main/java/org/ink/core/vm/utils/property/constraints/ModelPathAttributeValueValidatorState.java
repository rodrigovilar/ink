package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.PropertyValueValidatorState;
import org.ink.core.vm.constraints.ValidatorClassState;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.ValidatorMessages;

/**
 * @author Lior Schachter
 * 
 * 
 * 
 */
@ValidatorMessages(codes = { "invalid.model.path" }, messages = { CoreNotations.Ids.INVALID_MODELPATH })
@CoreClassSpec(metaclass = ValidatorClassState.class, javaMapping = JavaMapping.State_Behavior)
public interface ModelPathAttributeValueValidatorState extends PropertyValueValidatorState {

	public class Data extends PropertyValueValidatorState.Data implements ModelPathAttributeValueValidatorState {
	}

}
