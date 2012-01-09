package org.ink.core.vm.types;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.utils.InkNotations;

/**
 * @author Lior Schachter
 */
public class EnumTypeImpl<S extends EnumTypeState> extends InkObjectImpl<S> implements EnumType {

	private Class<?> typeClass;
	private Method getValueMethod;

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		typeClass = getContext().getFactory().resolveEnumClass(getState());
		try {
			getValueMethod = typeClass.getMethod(InkNotations.Reflection.VALUE_OF_METHOD_NAME, new Class[] { String.class });
		} catch (Exception e) {
			throw new CoreException("Could not find valueOf() method for enumeration " + getState().getId(), e);
		}
	}

	@Override
	public Object getEnumObject(String value) {
		try {
			return getValueMethod.invoke(null, new Object[] { value });
		} catch (Exception e) {
			throw new CoreException("Could not obtain enum value for string value " + value, e);
		}
	}

	@Override
	public DataTypeMarker getTypeMarker() {
		return DataTypeMarker.Enum;
	}

	@Override
	public final Class<?> getTypeClass() {
		return typeClass;
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public boolean isEnumeration() {
		return true;
	}

	@Override
	public List<String> getValues() {
		return Collections.unmodifiableList(getState().getValues());
	}

}
