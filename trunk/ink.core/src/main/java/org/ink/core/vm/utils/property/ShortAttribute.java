package org.ink.core.vm.utils.property;

/**
 * @author Lior Schachter
 */
public interface ShortAttribute extends NumericAttribute {

	@Override
	public Short getDefaultValue();

	@Override
	public Short getMaxValue();

	@Override
	public Short getMinValue();

	@Override
	public Short getFinalValue();
}
