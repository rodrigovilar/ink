package org.ink.core.vm.traits;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */
public enum TraitKind {

	Structural, // mandatory?
	Detachable, // optional?
	@CoreEnumField(isDefault = true)
	Structural_or_Detachable;

	public boolean isStructural() {
		return this == Structural | this == Structural_or_Detachable;
	}

	public boolean isDetachable() {
		return this == Detachable | this == Structural_or_Detachable;
	}
}
