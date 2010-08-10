package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.Property;
import org.ink.core.vm.types.CollectionType;


/**
 * @author Lior Schachter
 */
public interface CollectionProperty extends Property {
	
	@Override
	public CollectionType getType();
	public Integer getLowerBound();
	public Integer getUpperBound();
	
}
