package org.ink.core.vm.types;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InkObjectImpl;

/**
 * @author Lior Schachter
 */
public class CollectionTypeImpl<S extends CollectionTypeState> extends InkObjectImpl<S> implements CollectionType {

	private CollectionTypeMarker marker;
	private Class<?> typeClass;

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		marker = getState().getTypeMarker();
		typeClass = getContext().getFactory().resolveCollectionClass(marker);
	}

	@Override
	public final boolean isObject() {
		return false;
	}

	@Override
	public final boolean isPrimitive() {
		return false;
	}

	@Override
	public final boolean isCollection() {
		return true;
	}

	@Override
	public boolean isEnumeration() {
		return false;
	}

	@Override
	public final CollectionTypeMarker getCollectionTypeMarker() {
		return marker;
	}

	@Override
	public final DataTypeMarker getTypeMarker() {
		return DataTypeMarker.COLLECTION;
	}

	@Override
	public final Class<?> getJavaClass() {
		return typeClass;
	}
}
