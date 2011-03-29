package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirrorImpl;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.utils.property.ListPropertyState;


/**
 * @author Lior Schachter
 */
public class ListPropertyMirrorImpl<S extends ListPropertyMirrorState> extends CollectionPropertyMirrorImpl<S> implements ListPropertyMirror{

	private PropertyMirror itemMirror;

	public void boot(byte index, String name, Class<?> typeClass,
			DataTypeMarker typeMarker, boolean hasStaticValue,
			boolean isComputed, CollectionTypeMarker collectionMarker, PropertyMirror itemMirror) {
		super.boot(index, name, typeClass, typeMarker, hasStaticValue, isComputed, collectionMarker);
		this.itemMirror = itemMirror;
	}

	@Override
	public void bind(ClassMirror holdingClass, byte index) {
		super.bind(holdingClass, index);
		if(itemMirror!=null){
			itemMirror.bind(getDefiningClass(), getIndex());
		}
	}

	@Override
	public void afterTargetSet() {
		super.afterTargetSet();
		Property desc = ((ListPropertyState)getTargetState()).getListItem();
		if(desc!=null){
			itemMirror = desc.reflect();
		}
	}

	@Override
	public PropertyMirror getItemMirror(){
		return itemMirror;
	}

	@Override
	public boolean isValueContainsInkObject() {
		return itemMirror.isValueContainsInkObject();
	}

}
