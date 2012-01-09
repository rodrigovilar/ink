package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.Property;

/**
 * @author Lior Schachter
 */
public interface ValueProperty extends Property {
	public Object getDefaultValue();
}
