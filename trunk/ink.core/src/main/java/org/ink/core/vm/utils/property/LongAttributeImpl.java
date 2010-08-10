package org.ink.core.vm.utils.property;



/**
 * @author Lior Schachter
 */
public class LongAttributeImpl<S extends LongAttributeState> extends NumericAttributeImpl<S> implements LongAttribute {

	@Override
	public Long getMaxValue() {
		return getState().getMaxValue();
	}

	@Override
	public Long getMinValue() {
		return getState().getMinValue();
	}

	@Override
	public Long getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public Long getFinalValue() {
		return getState().getFinalValue();
	}

	
}
