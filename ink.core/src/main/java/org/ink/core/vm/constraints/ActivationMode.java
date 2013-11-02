package org.ink.core.vm.constraints;


/**
 * @author Lior Schachter
 */

public enum ActivationMode {
	ALWAYS("Always"), 
	DESIGN_TIME("Design_Time"), 
	RUN_TIME("Run_Time");
	
	public final String key;

	private ActivationMode(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	public static final ActivationMode enumValue(String val){
		return ActivationMode.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}
	
	
}
