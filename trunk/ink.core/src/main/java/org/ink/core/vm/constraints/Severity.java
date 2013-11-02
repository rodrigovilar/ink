package org.ink.core.vm.constraints;

/**
 * @author Lior Schachter
 */

public enum Severity {
	WARNING(10), ERROR(50), MAPPING_ERROR(51), INK_ERROR(52);

	private int level;

	private Severity(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}
	
	public static final Severity enumValue(String val){
		return Severity.valueOf(org.ink.core.vm.utils.CoreUtils.getJavaEnum(val));
	}

}
