package org.ink.core.vm.types;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObjectImpl;

/**
 * @author Lior Schachter
 */
public class PrimitiveTypeImpl<S extends PrimitiveTypeState> extends InkObjectImpl<S> implements PrimitiveType {

	private boolean isBoolean;
	private boolean isString;
	private boolean isNumeric;
	private boolean isDate;
	private PrimitiveTypeMarker marker;
	private Class<?> typeClass;

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		marker = getState().getTypeMarker();
		typeClass = getContext().getFactory().resolvePrimitiveClass(marker);
		switch (marker) {
		case Boolean:
			isBoolean = true;
			isString = false;
			isNumeric = false;
			isDate = false;
			break;
		case Byte:
			isBoolean = false;
			isString = false;
			isNumeric = true;
			isDate = false;
			break;
		case Date:
			isBoolean = false;
			isString = false;
			isNumeric = false;
			isDate = true;
			break;
		case Double:
			isBoolean = false;
			isString = false;
			isNumeric = true;
			isDate = false;
			break;
		case Float:
			isBoolean = false;
			isString = false;
			isNumeric = true;
			isDate = false;
			break;
		case Integer:
			isBoolean = false;
			isString = false;
			isNumeric = true;
			isDate = false;
			break;
		case Long:
			isBoolean = false;
			isString = false;
			isNumeric = true;
			isDate = false;
			break;
		case Short:
			isBoolean = false;
			isString = false;
			isNumeric = true;
			isDate = false;
			break;
		case String:
			isBoolean = false;
			isString = true;
			isNumeric = false;
			isDate = false;
			break;
		default:
			// do nothing - maybe taken care of by descendent
		}
	}

	@Override
	public final boolean isBoolean() {
		return isBoolean;
	}

	@Override
	public final Class<?> getTypeClass() {
		return typeClass;
	}

	@Override
	public final boolean isString() {
		return isString;
	}

	@Override
	public final boolean isNumeric() {
		return isNumeric;
	}

	@Override
	public final boolean isDate() {
		return isDate;
	}

	@Override
	public final PrimitiveTypeMarker getPrimitiveMarker() {
		return marker;
	}

	@Override
	public final boolean isObject() {
		return false;
	}

	@Override
	public final boolean isPrimitive() {
		return true;
	}

	@Override
	public final boolean isCollection() {
		return false;
	}

	@Override
	public boolean isEnumeration() {
		return false;
	}

	@Override
	public final DataTypeMarker getTypeMarker() {
		return DataTypeMarker.Primitive;
	}
}
