package org.ink.core.vm.lang.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lior Schachter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatorMessages {
	public String defaultMessage() default "";
	public String[] codes() default {};
	public String[] messages() default {};
}
