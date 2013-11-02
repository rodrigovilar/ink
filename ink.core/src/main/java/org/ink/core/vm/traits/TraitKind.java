package org.ink.core.vm.traits;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */
public enum TraitKind {

	STRUCTURAL("Structural"), // mandatory?
	DETACHABLE("Detachable"), // optional?
	@CoreEnumField(isDefault = true)
	STRUCTURAL_OR_DETACHABLE("Structural_or_Detachable");

	public final String key;

	private TraitKind(String key) {
		this.key = key;
	}

	
	public boolean isStructural() {
		return this == STRUCTURAL | this == STRUCTURAL_OR_DETACHABLE;
	}

	public boolean isDetachable() {
		return this == DETACHABLE | this == STRUCTURAL_OR_DETACHABLE;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final TraitKind enumValue(String val){
		return TraitKind.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}
}
