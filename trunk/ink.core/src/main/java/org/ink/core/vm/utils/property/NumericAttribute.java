package org.ink.core.vm.utils.property;



/**
 * @author Lior Schachter
 */
public interface NumericAttribute extends PrimitiveAttribute {
	
	public Number getMinValue();
	public Number getMaxValue();
	public Number getDefaultValue();
	public Number getFinalValue();
}
