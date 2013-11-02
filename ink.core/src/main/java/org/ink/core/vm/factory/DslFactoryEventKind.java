package org.ink.core.vm.factory;


public enum DslFactoryEventKind {
	START("START"), CLOSE("CLOSE"), RELOAD("RELOAD");
	public final String key;

	private DslFactoryEventKind(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final DslFactoryEventKind enumValue(String val){
		return DslFactoryEventKind.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}

}
