package org.ink.core.vm.utils.property;



/**
 * @author Lior Schachter
 */
public interface FloatAttribute extends NumericAttribute {
	
	@Override
	public Float getDefaultValue();
	@Override
	public Float getMaxValue();
	@Override
	public Float getMinValue();
	@Override
	public Float getFinalValue();
}
