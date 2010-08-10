package org.ink.core.vm.lang.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ink.core.vm.constraints.PropertyValueValidatorState;

/**
 * @author Lior Schachter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CorePropertySpec{

	public String[] keys();
	public Class<? extends PropertyValueValidatorState>[] validatorsClasses();

}
