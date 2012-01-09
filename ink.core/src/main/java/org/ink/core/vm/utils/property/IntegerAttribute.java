package org.ink.core.vm.utils.property;

/**
 * @author Lior Schachter
 */
public interface IntegerAttribute extends NumericAttribute {

	@Override
	public Integer getDefaultValue();

	@Override
	public Integer getMaxValue();

	@Override
	public Integer getMinValue();

	@Override
	public Integer getFinalValue();
}
