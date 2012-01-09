package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.PropertyValueValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.utils.property.NumericAttribute;

/**
 * @author Lior Schachter
 */
public class NumericAttributeValueValidatorImpl<S extends NumericAttributeValueValidatorState> extends ValidatorImpl<S> implements PropertyValueValidator {

	@Override
	public void validate(Property property, Object propertyValue, InkObjectState dataContainer, ValidationContext context, SystemState systemState) {
		if (propertyValue != null && !context.containsError()) {
			NumericAttribute att = (NumericAttribute) property;
			Number val = (Number) propertyValue;
			Number limit;
			if ((limit = att.getMinValue()) != null) {
				if (val.floatValue() < limit.floatValue()) {
					context.addError(dataContainer, this, "min.value.violation", property.getName(), limit);
					return;
				}
			}
			if ((limit = att.getMaxValue()) != null) {
				if (val.doubleValue() > limit.doubleValue()) {
					context.addError(dataContainer, this, "max.value.violation", property.getName(), limit);
					return;
				}
			}
		}
	}

}
