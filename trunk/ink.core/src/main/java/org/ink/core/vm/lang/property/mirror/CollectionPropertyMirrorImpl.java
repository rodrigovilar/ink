package org.ink.core.vm.lang.property.mirror;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.property.CollectionProperty;
import org.ink.core.vm.types.CollectionTypeMarker;



/**
 * @author Lior Schachter
 */
public class CollectionPropertyMirrorImpl<S extends CollectionPropertyMirrorState> extends PropertyMirrorImpl<S> implements CollectionPropertyMirror{
	
	private CollectionTypeMarker collectionTypeMarker;
	
	public void boot(byte index, String name, Class<?> typeClass,
			DataTypeMarker typeMarker, boolean hasStaticValue,
			boolean isComputed, CollectionTypeMarker collectionMarker) {
		super.boot(index, name, typeClass, typeMarker, hasStaticValue, isComputed);
		this.collectionTypeMarker = collectionMarker;
	}
	
	@Override
	public void afterTargetSet() {
		super.afterTargetSet();
		collectionTypeMarker = ((CollectionProperty)getTargetBehavior()).getType().getCollectionTypeMarker();
	}
	
	@Override
	public CollectionTypeMarker getCollectionTypeMarker() {
		return collectionTypeMarker;
	}
	
}
