package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

@CoreClassSpec(javaMapping = JavaMapping.STATE_BEHAVIOR)
public interface RootObjectRetrieverState extends
		org.ink.core.vm.lang.property.PropertyValueCalculatorState {
	public static final byte t_constraints = t_reflection + 1;

	public class Data extends
			org.ink.core.vm.lang.property.PropertyValueCalculatorState.Data
			implements RootObjectRetrieverState {
	}
}