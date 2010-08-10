package org.ink.core.vm.utils.property;



/**
 * @author Lior Schachter
 */
public class FloatAttributeImpl<S extends FloatAttributeState> extends NumericAttributeImpl<S> implements FloatAttribute {

	@Override
	public Float getMaxValue() {
		return getState().getMaxValue();
	}

	@Override
	public Float getMinValue() {
		return getState().getMinValue();
	}

	@Override
	public Float getDefaultValue() {
		return getState().getDefaultValue();
	}
	
	@Override
	public Float getFinalValue() {
		return getState().getFinalValue();
	}
	
}
