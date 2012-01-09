package org.ink.core.vm.lang.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lior Schachter
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CoreMapField {
	public enum Kind {
		key_value, elements;
	}

	public boolean mandatory() default false;

	public Kind kind() default Kind.elements;

	public String keyName();

	public String valueName();

	public boolean ordered() default false;

}
