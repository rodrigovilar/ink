package org.ink.core.vm.factory.internal;

import java.lang.reflect.Field;
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
	public void addPropertyClass(String fieldName, Class<?> cls);
	public Class<?> getPropertyClass(String fieldName);
	public Field[] getFields();
	public byte getNumberOfFields();
	public void setBehaviorClass(Class<?> behaviorClass);
	public byte getNumberOfTraits();
	public CoreClassSpec getMetadata();
	public void setProperties(Map<String, ? extends PropertyState> props);
	public void setPropertiesIndexes(Map<String, Byte> indexes);
	public Map<String, Byte> getPropertiesIndexes();
	public void setPropertyMirrors(PropertyMirror[] mirrors);
	public Map<String,  ? extends PropertyState> getProperties();
	public List<PropertyState> getPropertiesList();
	public PropertyMirror[] getPropertyMirrors();
	public Class<?> getBehaviorClass();
}