package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */
public enum JavaMapping {
	@CoreEnumField(isDefault=true)
	State_Behavior_Interface,
	State_Behavior,
	State_Interface,//only applicable for abstract dsl classes
	Behavior_Interface,
	Only_State,
	Only_Behavior,
	Only_Interface,
	No_Java;
	
	public boolean hasState(){
		return this==State_Behavior_Interface | this==JavaMapping.State_Behavior
					| this==Only_State | this==State_Interface;
	}
	
	public boolean hasBeahvior(){
		return this==State_Behavior_Interface | this==JavaMapping.State_Behavior
					| this==Only_Behavior | this==Behavior_Interface;
	}
	
	public boolean hasInterface(){
		return this==State_Behavior_Interface | this==JavaMapping.State_Interface
					| this==Only_Interface | this==Behavior_Interface;
	}
}
