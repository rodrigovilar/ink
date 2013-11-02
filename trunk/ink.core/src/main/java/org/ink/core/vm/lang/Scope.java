package org.ink.core.vm.lang;


/**
 * @author Lior Schachter
 */
public enum Scope {
	ALL("all"),
	DSL_PROTECTED("dsl_protected");
	
	public final String key;

	private Scope(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final Scope enumValue(String val){
		return Scope.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}

}
