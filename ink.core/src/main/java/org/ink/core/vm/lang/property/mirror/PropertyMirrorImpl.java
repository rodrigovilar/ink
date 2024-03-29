package org.ink.core.vm.lang.property.mirror;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InheritanceConstraints;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.InkType;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.MirrorImpl;

/**
 * @author Lior Schachter
 */
public class PropertyMirrorImpl<S extends PropertyMirrorState> extends MirrorImpl<S> implements PropertyMirror {

	private Class<?> typeClass;
	private DataTypeMarker typeMarker;
	private boolean hasStaticValue;
	private boolean isComputed;
	private boolean isFinal = false;
	private boolean isMutable = !isFinal;
	private String name;
	private byte index = Property.UNBOUNDED_PROPERTY_INDEX;
	private boolean isInherited = false;
	private ClassMirror definingClass;

	public void boot(byte index, String name, Class<?> typeClass, DataTypeMarker typeMarker, boolean hasStaticValue, boolean isComputed) {
		this.index = index;
		this.name = name;
		this.typeClass = typeClass;
		this.typeMarker = typeMarker;
		this.hasStaticValue = hasStaticValue;
		this.isComputed = isComputed;
	}

	@Override
	public void bind(ClassMirror holdingClass, byte index) {
		this.index = index;
		ClassMirror superClass = holdingClass.getSuper();
		if (index != Property.UNBOUNDED_PROPERTY_INDEX && superClass != null && superClass.getProperty(name) != null) {
			isInherited = true;
		} else {
			isInherited = false;
		}
		definingClass = holdingClass;
		Mirror classSuper = definingClass.getSuper();
		boolean found = false;
		while (!found && classSuper!=null) {
			if (!((ClassMirror) classSuper).getClassPropertiesIndexes().containsKey(name)) {
				found = true;
			} else {
				definingClass = (ClassMirror) classSuper;
			}
			classSuper = classSuper.getSuper();
		}
	}

	@Override
	public void afterTargetSet() {
		super.afterStateSet();
		Property prop = getTargetBehavior();
		name = prop.getName();
		InkType t = null;
		try{
			t = prop.getType();
		}catch(ClassCastException e){
			InkType type = ((org.ink.core.vm.mirror.ClassMirror)prop.getMeta().reflect()).getClassPropertyMirror(org.ink.core.vm.lang.PropertyState.p_type).getPropertyType();
			throw new CoreException("Invalid property type was defined for property '" +name+"'. Value should be of type '" + type.reflect().getId() +"'.");
		}
			
		isFinal = prop.getFinalValue() != null;
		isMutable = !isFinal;
		isComputed = false;
		hasStaticValue = true;
		// t==null when building up a property e.g. while creating a property in the editor
		if (t != null) {
			typeClass = t.getJavaClass();
			typeMarker = t.getTypeMarker();
		}
	}

	protected void setIsComputed(boolean value) {
		isComputed = value;
	}

	protected void setHasStaticValue(boolean value) {
		hasStaticValue = value;
	}

	@Override
	public final Class<?> getTypeClass() {
		return typeClass;
	}

	@Override
	public DataTypeMarker getTypeMarker() {
		return typeMarker;
	}

	@Override
	public boolean isComputed() {
		return isComputed;
	}

	@Override
	public boolean hasStaticValue() {
		return hasStaticValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte getIndex() {
		return index;
	}

	@Override
	public boolean isInherited() {
		return isInherited;
	}

	@Override
	public Object produceValue(InkObjectState container, Object value) {
		return value;
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public boolean isMutable() {
		return isMutable;
	}

	@Override
	public ClassMirror getDefiningClass() {
		return definingClass;
	}

	@Override
	public InheritanceConstraints getInheritanceConstraints() {
		return ((PropertyState) getTargetState()).getInheritanceConstraints();
	}

	@Override
	public boolean isValueDrillable() {
		switch (typeMarker) {
		case CLASS:
			return true;
		case COLLECTION:
			return true;
		}
		return false;
	}

	@Override
	public boolean isValueContainsInkObject() {
		return false;
	}

	@Override
	public InkType getPropertyType() {
		return ((PropertyState) getTargetState()).getType();
	}

	@Override
	public Class<?> getJavaPropertyType() {
		return Object.class;
	}

}
