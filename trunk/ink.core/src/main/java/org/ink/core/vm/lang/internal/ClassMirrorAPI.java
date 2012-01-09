package org.ink.core.vm.lang.internal;

import java.util.List;
import java.util.Map;

import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.ObjectFactory;
import org.ink.core.vm.lang.ObjectFactoryState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;

/**
 * @author Lior Schachter
 */
public interface ClassMirrorAPI extends MirrorAPI, InkClassState {

	public void applyProperties(List<Property> properties);

	public boolean isMetaClass();

	public void bootClass(InkClassState c, PropertyMirror[] propsMirrors, PropertyMirror[] intancePropsMirrors, Map<String, Byte> propertiesIndexes, Map<String, Byte> instancePropertiesIndexes, Class<?>[] behaviorProxyInterfaces, DslFactory context, Class<InkObjectState> dataClass);

	public Map<String, Byte> getClassPropertiesIndexes();

	public PropertyMirror[] getClassPropertiesMirrors();

	public Class<InkObjectState> getDataClass();

	public Class<? extends InkObject> getBehaviorClass();

	public Class<?>[] getBehaviorProxyInterfaces();

	public Class<InkObjectState> getStateInterface();

	public byte[] getRealPropertiesIndex();

	public void addRole(String namespace, String role, Trait t) throws WeaveException;

	public boolean hasRole(String role);

	public Trait getDetachableRole(byte index);

	public Byte getTraitIndex(String role);

	public Byte getTraitIndex(TraitClass traitClass);

	public int getTraitsCount();

	public Class<? extends InkObject> getInterfaceClass();

	public ObjectFactory getFactory();

	public ObjectFactoryState getFactoryState();

	public void setFactory(ObjectFactoryState value);

}
