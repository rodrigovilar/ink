package org.ink.codegen.velocity.class_gen;

import org.ink.core.vm.lang.property.PropertyValueCalculator;
import org.ink.core.vm.lang.property.PropertyValueCalculatorImpl;

public class EntryPointTargetRetrieverImpl<S extends EntryPointTargetRetrieverState>
		extends PropertyValueCalculatorImpl<S> implements
		PropertyValueCalculator {
	
	@Override
	protected <T extends org.ink.core.vm.lang.InkObjectState> Object calculate(T container, org.ink.core.vm.lang.Property property) {
		return null;
	}

}