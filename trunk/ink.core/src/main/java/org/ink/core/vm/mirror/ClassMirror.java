package org.ink.core.vm.mirror;

import java.util.List;
import java.util.Map;

import org.ink.core.vm.lang.ComponentType;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.ObjectFactory;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.operation.Operation;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.traits.Personality;
import org.ink.core.vm.traits.Trait;

/**
 * @author Lior Schachter
 */
public interface ClassMirror extends Mirror {

	public Map<String, Byte> getClassPropertiesIndexes();

	public PropertyMirror[] getClassPropertiesMirrors();

	public Map<String, PropertyMirror> getClassPropertiesMap();

	public Class<InkObjectState> getStateClass();

	public Class<? extends InkObject> getBehaviorClass();

	public boolean shouldCreateProxyOnBehaviorInstance();

	public InkObject getCachedBehavior(InkObjectState state);

	public void cacheBeahvior(InkObjectState state, InkObject behavior);

	public Class<?>[] getBehaviorProxyInterfaces();

	public Class<InkObjectState> getStateInterface();

	public Operation getMethod(String methodName, Object[] args);

	public Property getProperty(String propertyName);

	public Property getProperty(byte index);

	public Personality getPersonality();

	public boolean isMetaClass();

	public PropertyMirror getClassPropertyMirror(String propertyName);

	public PropertyMirror getClassPropertyMirror(byte index);

	public byte getTraitsCount();

	public boolean canCacheBehaviorInstance();

	public void cacheTrait(byte index, InkObjectState state, Trait trait);

	public <T extends Trait> T getTrait(byte index, InkObjectState state);

	public ObjectFactory getFactory();

	public boolean isPropertyFinal(byte index);

	public boolean isSubClassOf(InkClass otherClass);

	public boolean isSubClassOf(ClassMirror otherClassMirror);

	public List<? extends PropertyMirror> getOriginalProperties();

	public List<? extends PropertyMirror> getAllProperties();

	public boolean hasRole(String role);

	public Trait getRole(byte index);

	public JavaMapping getJavaMapping();

	public ComponentType getComponentType();

	public String getJavaPath();

	public String getFullJavaPackage();

	public boolean isStruct();
	
	public String getDescription();

}
