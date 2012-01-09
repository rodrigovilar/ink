package org.ink.core.vm.utils.property;

/**
 * @author Lior Schachter
 */
public class IntegerAttributeImpl<S extends IntegerAttributeState> extends NumericAttributeImpl<S> implements IntegerAttribute {

	@Override
	public Integer getMaxValue() {
		return getState().getMaxValue();
	}

	@Override
	public Integer getMinValue() {
		return getState().getMinValue();
	}

	@Override
	public Integer getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public Integer getFinalValue() {
		return getState().getFinalValue();
	}

}
