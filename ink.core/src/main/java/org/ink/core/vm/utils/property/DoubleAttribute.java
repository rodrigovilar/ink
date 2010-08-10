package org.ink.core.vm.utils.property;



/**
 * @author Lior Schachter
 */
public interface DoubleAttribute extends NumericAttribute {
	
	@Override
	public Double getDefaultValue();
	@Override
	public Double getMaxValue();
	@Override
	public Double getMinValue();
	@Override
	public Double getFinalValue();
}
