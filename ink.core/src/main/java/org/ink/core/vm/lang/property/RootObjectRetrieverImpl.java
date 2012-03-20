package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;

public class RootObjectRetrieverImpl<S extends RootObjectRetrieverState>
		extends PropertyValueCalculatorImpl<S> implements
		PropertyValueCalculator {

	@Override
	protected <T extends InkObjectState> Object calculate(T container, Property property) {
		return container.reflect().getRootOwner().getTargetBehavior();
	}

}