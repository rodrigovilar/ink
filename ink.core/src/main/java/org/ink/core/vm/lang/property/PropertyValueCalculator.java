package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;

/**
 * @author Lior Schachter
 */
public interface PropertyValueCalculator extends InkObject {

	public Object getValue(InkObjectState container, Property property, Object staticValue);

	public boolean hasStaticValue();

}
