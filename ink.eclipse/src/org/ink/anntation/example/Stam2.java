package org.ink.anntation.example;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Stam2 {

	public int ints();

	@ArrayInheritance(override = false)
	public int ints2() default 44;
}
