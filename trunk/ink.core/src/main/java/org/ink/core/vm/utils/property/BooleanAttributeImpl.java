package org.ink.core.vm.utils.property;



/**
 * @author Lior Schachter
 */
public class BooleanAttributeImpl<S extends BooleanAttributeState> extends PrimitiveAttributeImpl<S> implements BooleanAttribute {

	@Override
	public Boolean getDefaultValue() {
		return getState().getDefaultValue();
	}
	
	@Override
	public Boolean getFinalValue() {
		return getState().getFinalValue();
	}
	
}
