package org.ink.core.vm.types;


/**
 * @author Lior Schachter
 */
public enum ReferenceTypeMarker {
	OBJECT("Object"), STRUCT("Struct");
	
	public final String key;

	private ReferenceTypeMarker(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final ReferenceTypeMarker enumValue(String val){
		return ReferenceTypeMarker.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}
}
