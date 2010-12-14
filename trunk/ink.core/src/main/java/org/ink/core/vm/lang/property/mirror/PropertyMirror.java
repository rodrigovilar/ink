package org.ink.core.vm.lang.property.mirror;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.InheritanceConstraints;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.InkType;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;


/**
 * @author Lior Schachter
 */
public interface PropertyMirror extends Mirror{

	public void bind(ClassMirror holdingClass, byte index);
	public Class<?> getTypeClass();
	public DataTypeMarker getTypeMarker();
	public boolean isComputed();
	public boolean hasStaticValue();
	public String getName();
	public byte getIndex();
	public boolean isInherited();
	public boolean isFinal();
	public boolean isMutable();
	public Object produceValue(InkObjectState container, Object staticValue);
	public ClassMirror getDefiningClass();
	public InheritanceConstraints getInheritanceConstraints();
	public boolean isValueDrillable();
	public boolean isValueContainsInkObject();
	public InkType getPropertyType();


}
