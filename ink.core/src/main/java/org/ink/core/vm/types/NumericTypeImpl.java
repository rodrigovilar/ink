package org.ink.core.vm.types;

/**
 * @author Lior Schachter
 */
public class NumericTypeImpl<S extends NumericTypeState> extends PrimitiveTypeImpl<S> implements NumericType {

	private boolean isByte;
	private boolean isDouble;
	private boolean isFloat;
	private boolean isInteger;
	private boolean isLong;
	private boolean isShort;

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		isByte = false;
		isDouble = false;
		isFloat = false;
		isInteger = false;
		isLong = false;
		isShort = false;
		switch (getPrimitiveMarker()) {
		case Byte:
			isByte = true;
			break;
		case Double:
			isDouble = true;
			break;
		case Float:
			isFloat = true;
			break;
		case Integer:
			isInteger = true;
			break;
		case Long:
			isLong = true;
			break;
		case Short:
			isShort = true;
			break;

		}
	}

	@Override
	public boolean isByte() {
		return isByte;
	}

	@Override
	public boolean isDouble() {
		return isDouble;
	}

	@Override
	public boolean isFloat() {
		return isFloat;
	}

	@Override
	public boolean isInteger() {
		return isInteger;
	}

	@Override
	public boolean isLong() {
		return isLong;
	}

	@Override
	public boolean isShort() {
		return isShort;
	}

}
