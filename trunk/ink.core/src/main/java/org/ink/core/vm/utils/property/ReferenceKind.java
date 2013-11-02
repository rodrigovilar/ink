package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */
public enum ReferenceKind {
	ASSOCIATION_ONLY("Association_Only"), 
	COMPOSITION_ONLY("Composition_Only"), 
	@CoreEnumField(isDefault = true)
	ASSOCIATION_OR_COMPOSITION("Association_or_Composition");
	
	public final String key;

	private ReferenceKind(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final ReferenceKind enumValue(String val){
		return ReferenceKind.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}

}
