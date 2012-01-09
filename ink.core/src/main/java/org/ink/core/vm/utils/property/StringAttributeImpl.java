package org.ink.core.vm.utils.property;

/**
 * @author Lior Schachter
 */
public class StringAttributeImpl<S extends StringAttributeState> extends PrimitiveAttributeImpl<S> implements StringAttribute {

	@Override
	public Integer getMaxLength() {
		return getState().getMaxLength();
	}

	@Override
	public Integer getMinLength() {
		return getState().getMinLength();
	}

	@Override
	public String getRegularExpression() {
		return getState().getRegExp();
	}

	@Override
	public String getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public Object getFinalValue() {
		return getState().getFinalValue();
	}

}
