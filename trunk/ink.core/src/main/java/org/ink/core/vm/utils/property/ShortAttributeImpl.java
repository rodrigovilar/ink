package org.ink.core.vm.utils.property;

/**
 * @author Lior Schachter
 */
public class ShortAttributeImpl<S extends ShortAttributeState> extends NumericAttributeImpl<S> implements ShortAttribute {

	@Override
	public Short getMaxValue() {
		return getState().getMaxValue();
	}

	@Override
	public Short getMinValue() {
		return getState().getMinValue();
	}

	@Override
	public Short getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public Short getFinalValue() {
		return getState().getFinalValue();
	}

}
