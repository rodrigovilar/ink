package org.ink.core.vm.lang.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ink.core.vm.lang.InheritanceConstraints;

/**
 * @author Lior Schachter
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CoreField {

	public InheritanceConstraints valuePropagationStrategy() default InheritanceConstraints.INSTANCE_CAN_REFINE_INHERITED_VALUE;

	public boolean mandatory() default true;

	public String defaultValue() default "NO_VALUE";

	public boolean computed() default false;

	public boolean hasStaticValue() default false;
}
