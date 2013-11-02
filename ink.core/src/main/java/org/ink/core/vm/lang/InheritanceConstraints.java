package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */

public enum InheritanceConstraints {
	INSTANCE_MUST_OVERRIDE_INHERITED_VALUE("Instance_Must_Override_Inherited_Value"), @CoreEnumField(isDefault = true)
	INSTANCE_CAN_REFINE_INHERITED_VALUE("Instance_Can_Refine_Inherited_Value"), 
	INSTANCE_CAN_OVERRIDE_OR_REFINE_INHERITED_VALUE("Instance_Can_Override_or_Refine_Inherited_Value");
	
	public final String key;

	private InheritanceConstraints(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final InheritanceConstraints enumValue(String val){
		return InheritanceConstraints.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}

}
