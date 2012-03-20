package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.InkClass;

/**
 * @author Lior Schachter
 */
public class ModelPathAttributeImpl<S extends ModelPathAttributeState> extends PrimitiveAttributeImpl<S> implements ModelPathAttribute {

	@Override
	public String getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public String getFinalValue() {
		return getState().getFinalValue();
	}

	@Override
	public InkClass getPathRoot() {
		return getState().getPathRoot();
	}

}
