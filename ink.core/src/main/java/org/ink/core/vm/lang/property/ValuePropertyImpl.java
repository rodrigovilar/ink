package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.PropertyImpl;

/**
 * @author Lior Schachter
 */
public abstract class ValuePropertyImpl<S extends ValuePropertyState> extends PropertyImpl<S> implements ValueProperty {

	@Override
	public boolean isPlural() {
		return false;
	}

}
