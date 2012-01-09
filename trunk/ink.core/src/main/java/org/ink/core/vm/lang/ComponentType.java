package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */
public enum ComponentType {
	Root, Pure_Component, @CoreEnumField(isDefault = true)
	Root_or_Pure_Component;
}
