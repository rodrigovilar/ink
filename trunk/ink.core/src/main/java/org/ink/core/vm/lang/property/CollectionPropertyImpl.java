package org.ink.core.vm.lang.property;

import org.ink.core.vm.lang.PropertyImpl;
import org.ink.core.vm.types.CollectionType;


/**
 * @author Lior Schachter
 */
public abstract class CollectionPropertyImpl<S extends CollectionPropertyState> extends PropertyImpl<S> implements CollectionProperty {
	
	@Override
	public boolean isPlural() {
		return true;
	}
	
	@Override
	public Integer getLowerBound() {
		return getState().getLowerBound();
	}
	
	@Override
	public Integer getUpperBound() {
		return getState().getUpperBound();
	}
	
	@Override
	public CollectionType getType() {
		return getState().getType();
	}

}
