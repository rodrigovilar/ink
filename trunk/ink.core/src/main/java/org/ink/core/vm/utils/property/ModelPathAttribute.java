package org.ink.core.vm.utils.property;


/**
 * @author Lior Schachter
 */
public interface ModelPathAttribute extends PrimitiveAttribute {
	@Override
	public String getDefaultValue();
	@Override
	public String getFinalValue();
}
