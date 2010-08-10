
package org.ink.core.vm.factory.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;

/**
 * @author Lior Schachter
 */
public class CoreClassDescriptorImpl extends CoreObjectDescriptorImpl implements CoreClassDescriptor{

	private Map<String, Method> settersMap = new HashMap<String, Method>();
	private Field[] fields;
	Map<String, Field> fieldsMap = new HashMap<String, Field>();
	private CoreClassSpec metadata = null;
	private PropertyMirror[] mirrors = null;
	private List<PropertyState> props = null;
	private Map<String, Byte> indexes = null;
	private byte numberOfTraits;
	private Class<?> behaviorClass;
	
	public CoreClassDescriptorImpl(String id, String classId, Class<?> stateClass,
			InkObjectState object, Field[] fields, byte numberOfTraits) {
		super(id, classId, stateClass, object);
		this.fields = fields;
		for(Field f : fields){
			fieldsMap.put(f.getName(), f);
		}
		this.numberOfTraits = numberOfTraits;
	}
	
	/* (non-Javadoc)
	 * @see org.ink.core.vm.internal.CoreClassDescriptor#getFields()
	 */
	public Field[] getFields(){
		return fields;
	}
	
	public Field getField(String name){
		return fieldsMap.get(name);
	}
	
	/* (non-Javadoc)
	 * @see org.ink.core.vm.internal.CoreClassDescriptor#getNumberOfFields()
	 */
	public byte getNumberOfFields(){
		return (byte)fields.length;
	}

	@Override
	public String toString() {
		String result = super.toString();
		result+= ". " + getNumberOfFields() + " Properties : ";
		//getObject().getPropertyValue(InkClassState.p_properties);
		return result;
	}
	
	@Override
	public boolean isClass() {
		return true;
	}

	public void setMetadata(CoreClassSpec metadata){
		this.metadata = metadata;
	}
	
	public CoreClassSpec getMetadata(){
		return metadata;
	}

	@Override
	public PropertyMirror[] getPropertyMirrors() {
		return mirrors;
	}

	@Override
	public void setPropertyMirrors(PropertyMirror[] mirrors) {
		this.mirrors = mirrors;
	}

	public List<PropertyState> getProperties() {
		return props;
	}

	public void setProperties(List<PropertyState> props) {
		this.props = props;
	}

	@Override
	public CoreField getFieldAnntation(String name) {
		Field f = fieldsMap.get(name);
		if(f!=null){
			return f.getAnnotation(CoreField.class);
		}
		return null;
	}

	@Override
	public Map<String, Byte> getPropertiesIndexes() {
		return indexes;
	}

	@Override
	public void setPropertiesIndexes(Map<String, Byte> indexes) {
		this.indexes = indexes;
	}
	
	@Override
	public ClassMirrorAPI getObject() {
		return (ClassMirrorAPI)super.getObject();
	}

	@Override
	public byte getNumberOfTraits() {
		return numberOfTraits;
	}

	@Override
	public Class<?> getBehaviorClass() {
		return behaviorClass;
	}
	
	@Override
	public void setBehaviorClass(Class<?> behaviorClass) {
		this.behaviorClass = behaviorClass;
	}

	@Override
	public void addSetter(String fieldName, Method setter) {
		settersMap.put(fieldName, setter);
	}
	
	@Override
	public Method getSettter(String fieldName){
		return settersMap.get(fieldName);
	}

	
}
