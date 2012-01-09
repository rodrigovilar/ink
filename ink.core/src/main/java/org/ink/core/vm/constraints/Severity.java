package org.ink.core.vm.constraints;

/**
 * @author Lior Schachter
 */

public enum Severity {
	Warning(10), Error(50), MAPPING_ERROR(51), INK_ERROR(52);

	private int level;

	private Severity(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

}
