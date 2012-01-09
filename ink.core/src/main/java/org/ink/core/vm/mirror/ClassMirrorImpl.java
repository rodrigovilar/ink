package org.ink.core.vm.mirror;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ink.core.vm.lang.ComponentType;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.ObjectFactory;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.operation.Operation;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.traits.Personality;
import org.ink.core.vm.traits.Trait;

/**
 * @author Lior Schachter
 */
public class ClassMirrorImpl<S extends ClassMirrorState> extends MirrorImpl<S> implements ClassMirror {

	@Override
	public Map<String, Byte> getClassPropertiesIndexes() {
		return ((ClassMirrorAPI) getTargetState()).getClassPropertiesIndexes();
	}

	@Override
	public PropertyMirror[] getClassPropertiesMirrors() {
		return ((ClassMirrorAPI) getTargetState()).getClassPropertiesMirrors();
	}

	@Override
	public boolean isMetaClass() {
		return ((ClassMirrorAPI) getTargetState()).isMetaClass();
	}

	@Override
	public Map<String, PropertyMirror> getClassPropertiesMap() {
		PropertyMirror[] props = getClassPropertiesMirrors();
		Map<String, PropertyMirror> result = new HashMap<String, PropertyMirror>(props.length);
		for (PropertyMirror pm : props) {
			result.put(pm.getName(), pm);
		}
		return result;
	}

	@Override
	public Class<InkObjectState> getStateClass() {
		return ((ClassMirrorAPI) getTargetState()).getDataClass();
	}

	@Override
	public Class<? extends InkObject> getBehaviorClass() {
		ClassMirrorAPI target = (ClassMirrorAPI) getTargetState();
		if (target.isAbstract()) {
			return ((ClassMirror) target.reflect().getSuper()).getBehaviorClass();
		}
		return ((ClassMirrorAPI) getTargetState()).getBehaviorClass();
	}

	@Override
	public boolean shouldCreateProxyOnBehaviorInstance() {
		List<? extends Operation> ops = ((ClassMirrorAPI) getTargetState()).getOperations();
		if (ops != null) {
			return !ops.isEmpty();
		}
		return false;
	}

	@Override
	public void cacheBeahvior(InkObjectState state, InkObject behavior) {
		((MirrorAPI) state).cacheBeahvior(behavior);
	}

	@Override
	public void cacheTrait(byte index, InkObjectState state, Trait trait) {
		((MirrorAPI) state).cacheTrait(index, trait);
	}

	@Override
	public <T extends Trait> T getTrait(byte index, InkObjectState state) {
		return ((MirrorAPI) state).getCachedTrait(index);
	}

	@Override
	public InkObject getCachedBehavior(InkObjectState state) {
		return ((MirrorAPI) state).getCachedBehavior();
	}

	@Override
	public Class<?>[] getBehaviorProxyInterfaces() {
		return ((ClassMirrorAPI) getTargetState()).getBehaviorProxyInterfaces();
	}

	@Override
	public Operation getMethod(String methodName, Object[] args) {
		List<? extends Operation> allOps = ((InkClassState) getTargetState()).getOperations();
		if (allOps != null) {
			for (Operation op : allOps) {
				if (op.getName().equals(methodName)) {
					return op;
				}
			}
		}
		return null;
	}

	@Override
	public Property getProperty(String propertyName) {
		Byte index = ((ClassMirrorAPI) getTargetState()).getClassPropertiesIndexes().get(propertyName);
		if (index != null) {
			return (Property) getAllProperties().get(index).getTargetBehavior();
		}
		return null;
	}

	@Override
	public Property getProperty(byte index) {
		return ((InkClassState) getTargetState()).getProperties().get(index);
	}

	@Override
	public PropertyMirror getClassPropertyMirror(String propertyName) {
		Byte index = ((ClassMirrorAPI) getTargetState()).getClassPropertiesIndexes().get(propertyName);
		if (index != null) {
			return getClassPropertyMirror(index);
		}
		return null;
	}

	@Override
	public PropertyMirror getClassPropertyMirror(byte index) {
		return ((ClassMirrorAPI) getTargetState()).getClassPropertiesMirrors()[index];
	}

	@Override
	public byte getTraitsCount() {
		return ((InkClassState) getTargetState()).getPersonality().getTraitsCount();
	}

	@Override
	public boolean canCacheBehaviorInstance() {
		return ((InkClassState) getTargetState()).getCanCacheBehaviorInstance();
	}

	@Override
	public Class<InkObjectState> getStateInterface() {
		return ((ClassMirrorAPI) getTargetState()).getStateInterface();
	}

	@Override
	public boolean isStruct() {
		return false;
	}

	@Override
	public ObjectFactory getFactory() {
		return ((ClassMirrorAPI) getTargetState()).getFactory();
	}

	@Override
	public boolean isPropertyFinal(byte index) {
		return getFactory().isPropertyFinal(index);
	}

	@Override
	public List<? extends PropertyMirror> getOriginalProperties() {
		InkClassState target = getTargetState();
		Map<String, ? extends Property> properties = target.getProperties();
		List<PropertyMirror> result = new ArrayList<PropertyMirror>(properties.size());
		for (Property prop : properties.values()) {
			result.add((PropertyMirror) prop.reflect());
		}
		return result;
	}

	@Override
	public List<? extends PropertyMirror> getAllProperties() {
		ClassMirror target = getTargetState().reflect();
		List<? extends PropertyMirror> result = Arrays.asList(target.getClassPropertiesMirrors());
		return result;
	}

	@Override
	public boolean isSubClassOf(ClassMirror otherClass) {
		if (otherClass.getId().equals(getId())) {
			return true;
		}
		Mirror zuper = getSuper();
		while (zuper != null && zuper.isClass()) {
			if (zuper.getId().equals(otherClass.getId())) {
				return true;
			}
			zuper = zuper.getSuper();
		}
		return false;
	}

	@Override
	public boolean hasRole(String role) {
		return ((ClassMirrorAPI) getTargetState()).hasRole(role);
	}

	@Override
	public boolean isSubClassOf(InkClass otherClass) {
		return isSubClassOf((ClassMirror) otherClass.reflect());
	}

	@Override
	public Trait getRole(byte index) {
		if (index < getTraitsCount()) {
			return ((InkClassState) getTargetState()).getPersonality().getTrait(index);
		} else {
			return ((ClassMirrorAPI) getTargetState()).getDetachableRole(index);
		}
	}

	@Override
	public JavaMapping getJavaMapping() {
		return ((ClassMirrorAPI) getTargetState()).getJavaMapping();
	}

	@Override
	public ComponentType getComponentType() {
		return ((ClassMirrorAPI) getTargetState()).getComponentType();
	}

	@Override
	public String getJavaPath() {
		return ((ClassMirrorAPI) getTargetState()).getJavaPath();
	}

	@Override
	public String getFullJavaPackage() {
		String result = getTragetOwnerFactory().getJavaPackage();
		String ownPath = getJavaPath();
		if (ownPath == null || ownPath.equals("")) {
			return result;
		}
		return result + "." + ownPath;
	}

	@Override
	public Personality getPersonality() {
		return ((ClassMirrorAPI) getTargetState()).getPersonality();
	}

}
