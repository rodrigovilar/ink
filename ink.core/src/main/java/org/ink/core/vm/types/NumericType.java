package org.ink.core.vm.types;

/**
 * @author Lior Schachter
 */
public interface NumericType extends PrimitiveType {
	public boolean isLong();

	public boolean isDouble();

	public boolean isByte();

	public boolean isShort();

	public boolean isInteger();

	public boolean isFloat();
}
