package org.ink.core.vm.utils.property;



/**
 * @author Lior Schachter
 */
public interface LongAttribute extends NumericAttribute {
	
	@Override
	public Long getDefaultValue();
	@Override
	public Long getMaxValue();
	@Override
	public Long getMinValue();
	@Override
	public Long getFinalValue();
}
