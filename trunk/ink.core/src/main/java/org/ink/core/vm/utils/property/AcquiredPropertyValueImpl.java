package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.PropertyValueCalculatorImpl;
import org.ink.core.vm.mirror.Mirror;

/**
 * @author Lior Schachter
 */
public abstract class AcquiredPropertyValueImpl<S extends AcquiredPropertyValueState> extends PropertyValueCalculatorImpl<S> implements AcquiredPropertyValue {

	public Object calculate(InkObjectState container, Property property) {
		Mirror owner = container.reflect().getOwner();
		Object result = null;
		while (result == null && owner != null) {
			result = owner.getPropertyValue(property.getName());
			owner = owner.getOwner();
		}
		return result;
	}

}