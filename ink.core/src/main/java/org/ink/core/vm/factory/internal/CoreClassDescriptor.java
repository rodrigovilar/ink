package org.ink.core.vm.factory.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;

/**
 * @author Lior Schachter
 */
public interface CoreClassDescriptor extends CoreObjectDescriptor{

	@Override
	public ClassMirrorAPI getObject();
	public CoreField getFieldAnntation(String name);
	public Field getField(String name);
	public void addSetter(String fieldName, Method setter);
	public Method getSettter(String fieldName);
	public Field[] getFields();
	public byte getNumberOfFields();
	public void setBehaviorClass(Class<?> behaviorClass);
	public byte getNumberOfTraits();
	public CoreClassSpec getMetadata();
	public void setProperties(List<PropertyState> props);
	public void setPropertiesIndexes(Map<String, Byte> indexes);
	public Map<String, Byte> getPropertiesIndexes();
	public void setPropertyMirrors(PropertyMirror[] mirrors);
	public List<PropertyState> getProperties();
	public PropertyMirror[] getPropertyMirrors();
	public Class<?> getBehaviorClass();
	
}