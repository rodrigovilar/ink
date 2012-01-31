package org.ink.codegen.velocity.class_gen;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.PropertyValueCalculator;
import org.ink.core.vm.lang.property.PropertyValueCalculatorImpl;

public class RootObjectRetrieverImpl<S extends RootObjectRetrieverState>
		extends PropertyValueCalculatorImpl<S> implements
		PropertyValueCalculator {

	@Override
	protected <T extends InkObjectState> Object calculate(T container, Property property) {
		return container.reflect().getRootOwner().getTargetBehavior();
	}

}