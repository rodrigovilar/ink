package org.ink.core.vm.constraints;

/**
 * @author Lior Schachter
 */
public enum SystemState {
	DESIGN_TIME("Design_Time"), RUN_TIME("Run_Time");
	
	public final String key;

	private SystemState(String key) {
		this.key = key;
	}

	
	@Override
	public String toString() {
		return key;
	}
}
