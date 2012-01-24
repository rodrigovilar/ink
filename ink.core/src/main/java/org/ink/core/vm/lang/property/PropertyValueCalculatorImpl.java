package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;

/**
 * @author Lior Schachter
 */
public class PropertyValueCalculatorImpl<S extends PropertyValueCalculatorState> extends InkObjectImpl<S> implements PropertyValueCalculator {

	@Override
	public boolean hasStaticValue() {
		Boolean result = getState().getHasStaticValue();
		if (result == null) {
			return false;
		}
		return result;
	}

	protected <T extends InkObjectState> Object calculate(T container, Property property){return null;}

	@Override
	public Object getValue(InkObjectState container, Property property, Object staticValue) {
		if (hasStaticValue() && staticValue != null) {
			return staticValue;
		}
		return calculate(container, property);
	}

}
