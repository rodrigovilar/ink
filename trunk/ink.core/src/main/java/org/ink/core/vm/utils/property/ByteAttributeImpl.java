package org.ink.core.vm.utils.property;

/**
 * @author Lior Schachter
 */
public class ByteAttributeImpl<S extends ByteAttributeState> extends NumericAttributeImpl<S> implements ByteAttribute {

	@Override
	public Byte getMaxValue() {
		return getState().getMaxValue();
	}

	@Override
	public Byte getMinValue() {
		return getState().getMinValue();
	}

	@Override
	public Byte getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public Byte getFinalValue() {
		return getState().getFinalValue();
	}

}
