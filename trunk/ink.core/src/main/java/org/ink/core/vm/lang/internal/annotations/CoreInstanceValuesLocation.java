package org.ink.core.vm.lang.internal.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Lior Schachter
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CoreInstanceValuesLocation {
	public byte[] indexes();
}
