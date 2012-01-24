package org.ink.core.vm.lang.property.mirror;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.PropertyValueCalculator;
import org.ink.core.vm.lang.property.ValuePropertyState;

/**
 * @author Lior Schachter
 */
public class ValuePropertyMirrorImpl<S extends ValuePropertyMirrorState> extends PropertyMirrorImpl<S> implements ValuePropertyMirror {

	private PropertyValueCalculator calculator;

	@Override
	public void afterTargetSet() {
		super.afterTargetSet();
		calculator = ((ValuePropertyState) getTargetState()).getValueCalculator();
		if (calculator != null) {
			setIsComputed(true);
			setHasStaticValue(calculator.hasStaticValue());
		}
	}

	@Override
	public Object produceValue(InkObjectState container, Object value) {
		if (isComputed()) {
			return calculator.getValue(container, (Property) getTargetBehavior(), value);
		}
		return super.produceValue(container, value);
	}

}
