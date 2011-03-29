package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirrorImpl;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.utils.property.MapPropertyState;


/**
 * @author Lior Schachter
 */
public class MapPropertyMirrorImpl<S extends MapPropertyMirrorState> extends CollectionPropertyMirrorImpl<S> implements MapPropertyMirror{

	private PropertyMirror valueMirror;
	private PropertyMirror keyMirror;

	public void boot(byte index, String name, Class<?> typeClass,
			DataTypeMarker typeMarker, boolean hasStaticValue,
			boolean isComputed, CollectionTypeMarker collectionMarker, PropertyMirror valueMirror, PropertyMirror keyMirror) {
		super.boot(index, name, typeClass, typeMarker, hasStaticValue, isComputed, collectionMarker);
		this.valueMirror = valueMirror;
		this.keyMirror = keyMirror;
	}


	@Override
	public void afterTargetSet() {
		super.afterTargetSet();
		Property desc = ((MapPropertyState)getTargetState()).getMapValue();
		if(desc!=null){
			valueMirror = (PropertyMirror)desc.reflect();
		}
		desc = ((MapPropertyState)getTargetState()).getMapKey();
		if(desc!=null){
			keyMirror = (PropertyMirror)desc.reflect();
		}
	}

	@Override
	public void bind(ClassMirror holdingClass, byte index) {
		super.bind(holdingClass, index);
		if(valueMirror!=null){
			valueMirror.bind(getDefiningClass(), getIndex());
		}
		if(keyMirror!=null){
			keyMirror.bind(getDefiningClass(), getIndex());
		}
	}

	@Override
	public PropertyMirror getKeyMirror() {
		return keyMirror;
	}

	@Override
	public PropertyMirror getValueMirror() {
		return valueMirror;
	}

	@Override
	public boolean isValueContainsInkObject() {
		return valueMirror.isValueContainsInkObject() || keyMirror.isValueContainsInkObject();
	}


}
