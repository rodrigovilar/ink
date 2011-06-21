package org.ink.core.vm.utils.property.mirror;

import java.util.HashMap;
import java.util.Map;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirrorImpl;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.utils.property.Dictionary;
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
		if(valueMirror==null || keyMirror==null){
			System.out.println("");
		}
		this.valueMirror = valueMirror;
		this.keyMirror = keyMirror;
	}



	public void init() {
		Dictionary dic = ((MapPropertyState)getTargetState()).getSpecifications();
		if(dic!=null){
			keyMirror = dic.getKeyMirror();
			valueMirror = dic.getValueMirror();
		}
	}

	@Override
	public void bind(ClassMirror holdingClass, byte index) {
		super.bind(holdingClass, index);
		if(getValueMirror()!=null){
			valueMirror.bind(getDefiningClass(), getIndex());
		}
		if(getKeyMirror()!=null){
			keyMirror.bind(getDefiningClass(), getIndex());
		}
	}

	@Override
	public PropertyMirror getKeyMirror() {
		if(keyMirror==null){
			init();
		}
		return keyMirror;
	}

	@Override
	public PropertyMirror getValueMirror() {
		if(valueMirror==null){
			init();
		}
		return valueMirror;
	}

	@Override
	public boolean isValueContainsInkObject() {
		return getValueMirror().isValueContainsInkObject() || keyMirror.isValueContainsInkObject();
	}


	@Override
	public Map<?, ?> getNewInstance() {
		Dictionary dic = ((MapPropertyState)getTargetState()).getSpecifications();
		if(dic!=null){
			return dic.getNewInstance();
		}
		return new HashMap<Object, Object>();
	}


	@Override
	public Dictionary getSpecifictation() {
		return ((MapPropertyState)getTargetState()).getSpecifications();
	}


}
