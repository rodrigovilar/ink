package org.ink.core.vm.utils.property;

/**
 * @author Lior Schachter
 */
public interface StringAttribute extends PrimitiveAttribute {
	public Integer getMinLength();

	public Integer getMaxLength();

	public String getRegularExpression();

	@Override
	public String getDefaultValue();
	
	@Override
	public String getFinalValue();
}
