package org.ink.core.vm.utils.property.constraints;

import java.util.Collection;
import java.util.Iterator;

import org.ink.core.vm.constraints.PropertyValueValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.utils.property.EnumAttribute;

/**
 * @author Lior Schachter
 */
public class EnumAttributeValueValidatorImpl<S extends EnumAttributeValueValidatorState> extends ValidatorImpl<S> implements PropertyValueValidator {

	@Override
	public void validate(Property property, Object propertyValue, InkObjectState dataContainer, ValidationContext context, SystemState systemState) {
		if (propertyValue != null && !context.containsError()) {
			EnumAttribute att = (EnumAttribute) property;
			String value = propertyValue.toString();
			if (value != null) {
				EnumType eType = att.getType();
				Collection<String> vals = eType.getValues();
				if (!vals.contains(value)) {
					String values = "";
					Iterator<String> iter = vals.iterator();
					for (int i = 0; i < vals.size(); i++) {
						values += iter.next();
						if (i < vals.size() - 1) {
							values += ",";
						}
					}
					context.addError(dataContainer, this, "enum.illegal.value", value, eType.reflect().getId(), values);
				}

			}
		}
	}

}
