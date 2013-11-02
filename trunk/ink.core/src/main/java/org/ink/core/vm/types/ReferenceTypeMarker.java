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
}
