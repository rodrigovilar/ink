package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.property.AttributeImpl;
import org.ink.core.vm.types.EnumType;


/**
 * @author Lior Schachter
 */
public class EnumAttributeImpl<S extends EnumAttributeState> extends AttributeImpl<S> implements EnumAttribute {

	private Object defaultValue;
	private Object finalValue;
	
	@Override
	public EnumType getType() {
		return getState().getType();
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public Object getFinalValue() {
		return finalValue;
	}
	
	@Override
	public void afterStateSet() {
		super.afterStateSet();
		String originalValue = getState().getDefaultValue();
		if(originalValue!=null){
			this.defaultValue = getState().getType().getEnumObject(originalValue);
		}else{
			this.defaultValue = null;
		}
		originalValue = getState().getFinalValue();
		if(originalValue!=null){
			this.finalValue = getState().getType().getEnumObject(originalValue);
		}else{
			this.finalValue = null;
		}
	}
}
