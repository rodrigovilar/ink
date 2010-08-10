package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;


/**
 * @author Lior Schachter
 */
public interface PropertyConstraints extends Constraints{
	public boolean validatePropertyValue(Property property, Object propertyValue, InkObjectState dataContainer,
			ValidationContext context, SystemState systemState);
}
