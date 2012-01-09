package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.property.PropertyValueCalculatorState;

/**
 * @author Lior Schachter
 */
public interface AcquiredPropertyValueState extends PropertyValueCalculatorState {

	public class Data extends PropertyValueCalculatorState.Data implements AcquiredPropertyValueState {
	}

}
