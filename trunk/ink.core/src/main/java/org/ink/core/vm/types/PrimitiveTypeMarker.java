package org.ink.core.vm.types;


/**
 * @author Lior Schachter
 */
public enum PrimitiveTypeMarker {
	BYTE("Byte"), SHORT("Short"), INTEGER("Integer"), LONG("Long"), FLOAT("Float"), DOUBLE("Double"), STRING("String"), DATE("Date"), BOOLEAN("Boolean");
	
	public final String key;

	private PrimitiveTypeMarker(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final PrimitiveTypeMarker enumValue(String val){
		return PrimitiveTypeMarker.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}
}
