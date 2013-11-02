package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */
public enum JavaMapping {
	@CoreEnumField(isDefault = true)
	STATE_BEHAVIOR_INTERFACE("State_Behavior_Interface"), 
	STATE_BEHAVIOR("State_Behavior"), 
	STATE_INTERFACE("State_Interface"), // only applicable for abstract dsl classes
	BEHAVIOR_INTERFACE("Behavior_Interface"), 
	ONLY_STATE("Only_State"), 
	ONLY_BEHAVIOR("Only_Behavior"), 
	ONLY_INTERFACE("Only_Interface"), 
	NO_JAVA("No_Java");

	public final String key;

	private JavaMapping(String key) {
		this.key = key;
	}

	public boolean hasJava() {
		return this != NO_JAVA;
	}

	public boolean hasState() {
		return this == STATE_BEHAVIOR_INTERFACE | this == STATE_BEHAVIOR| this == ONLY_STATE | this == STATE_INTERFACE;
	}

	public boolean hasBehavior() {
		return this == STATE_BEHAVIOR_INTERFACE | this == STATE_BEHAVIOR | this == ONLY_BEHAVIOR | this == BEHAVIOR_INTERFACE;
	}

	public boolean hasInterface() {
		return this == STATE_BEHAVIOR_INTERFACE | this == JavaMapping.STATE_INTERFACE | this == ONLY_INTERFACE | this == BEHAVIOR_INTERFACE;
	}

	public JavaMapping withState() {
		JavaMapping result = null;
		if (hasState()) {
			result = this;
		} else if (this == NO_JAVA) {
			result = ONLY_STATE;
		} else if (hasBehavior() && hasInterface()) {
			result = STATE_BEHAVIOR_INTERFACE;
		} else if (hasBehavior()) {
			result = STATE_BEHAVIOR;
		} else {
			result = STATE_INTERFACE;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final JavaMapping enumValue(String val){
		return JavaMapping.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}
}
