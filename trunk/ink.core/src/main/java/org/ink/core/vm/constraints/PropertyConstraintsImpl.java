package org.ink.core.vm.constraints;

import java.util.Collection;
import java.util.Map;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;

/**
 * @author Lior Schachter
 */
public class PropertyConstraintsImpl<S extends PropertyConstraintsState> extends ConstraintsImpl<S> implements PropertyConstraints {

	@Override
	public boolean validatePropertyValue(Property property, Object propertyValue, InkObjectState dataContainer, ValidationContext context, SystemState systemState) {
		Property prop = getTargetBehavior();
		getState().getGenericPropertyValueConstraints().validate(prop, propertyValue, dataContainer, context, systemState);
		if (!context.containsError()) {
			Map<String, PropertyValueValidator> validatorsMap = getState().getPropertyValueValidators();
			if (validatorsMap != null) {
				Collection<PropertyValueValidator> validators = validatorsMap.values();
				if (!validators.isEmpty()) {
					for (PropertyValueValidator v : validators) {
						v.validate(prop, propertyValue, dataContainer, context, systemState);
						if (context.aborted()) {
							return false;
						}
					}
				}
				return !context.containsError();
			}
			return true;
		} else {
			return false;
		}
	}

}
