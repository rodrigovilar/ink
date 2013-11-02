package org.ink.core.vm.types;


/**
 * @author Lior Schachter
 */
public enum CollectionTypeMarker {
	LIST("LIST"), MAP("MAP");
	
	public final String key;

	private CollectionTypeMarker(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final CollectionTypeMarker enumValue(String val){
		return CollectionTypeMarker.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}
}
