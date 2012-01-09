package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */

public enum InheritanceConstraints {
	Instance_Must_Override_Inherited_Value, @CoreEnumField(isDefault = true)
	Instance_Can_Refine_Inherited_Value, Instance_Can_Override_or_Refine_Inherited_Value;
}
