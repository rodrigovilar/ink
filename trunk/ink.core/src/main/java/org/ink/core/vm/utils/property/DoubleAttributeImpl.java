package org.ink.core.vm.utils.property;



/**
 * @author Lior Schachter
 */
public class DoubleAttributeImpl<S extends DoubleAttributeState> extends NumericAttributeImpl<S> implements DoubleAttribute {

	@Override
	public Double getMaxValue() {
		return getState().getMaxValue();
	}

	@Override
	public Double getMinValue() {
		return getState().getMinValue();
	}

	@Override
	public Double getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public Double getFinalValue() {
		return getState().getFinalValue();
	}
	
}
