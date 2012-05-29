package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.PropertyValueValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.exceptions.InvalidPathException;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public class ModelPathAttributeValueValidatorImpl<S extends ModelPathAttributeValueValidatorState> extends ValidatorImpl<S> implements PropertyValueValidator {

	@Override
	public void validate(Property property, Object propertyValue, InkObjectState dataContainer, ValidationContext context, SystemState systemState) {
		if (propertyValue != null && !context.containsError()) {
			String path = (String) propertyValue;
			Mirror m = dataContainer.reflect().getRootOwner();
			try{
				Object value = m.getValueByPath(path);
			}catch(InvalidPathException e){
				context.addError(dataContainer, this, "invalid.model.path",path, e.getMessage());	
			}
		}
	}

}
