package org.ink.core.vm.utils.property;

/**
 * @author Lior Schachter
 */
public interface ByteAttribute extends NumericAttribute {

	@Override
	public Byte getDefaultValue();

	@Override
	public Byte getMaxValue();

	@Override
	public Byte getMinValue();

	@Override
	public Byte getFinalValue();
}
