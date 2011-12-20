package org.ink.core.vm.mirror;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.ElementDescriptor;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.types.ObjectTypeMarker;

/**
 * @author Lior Schachter
 */
public interface Mirror extends Trait{
	public ObjectTypeMarker getObjectTypeMarker();
	public String getId();
	public String getShortId();
	public String getNamespace();
	public <T extends ObjectEditor> T edit();
	public <T extends ObjectEditor> T edit(boolean transactional);
	public ClassMirror getClassMirror();
	public <T extends InkObjectState> T getTarget();
	public <M extends Mirror> M getOwner();
	public <M extends Mirror> M getRootOwner();
	public <M extends Mirror> M getSuper();
	public boolean isAbstract();
	public PropertyMirror getPropertyMirror(byte index);
	public PropertyMirror getPropertyMirror(String name);
	public PropertyMirror[] getPropertiesMirrors();
	public Object getPropertyValue(String propertyName);
	public Object getPropertyValue(byte index);
	public Object getPropertyStaticValue(byte index);
	public byte getPropertiesCount();
	public Scope getScope();
	public PropertyMirror getDefiningProperty();
	public byte getDefiningPropertyIndex();
	public <T extends InkObjectState> T cloneTargetState();
	public <T extends InkObjectState> T cloneTargetState(boolean identicalTwin);
	public boolean isRoot();
	public boolean isClass();
	public boolean isInstanceOf(InkClass cls);
	public boolean isLoadOnStartup();
	public boolean isCoreObject();
	public Object get(Object key);
	public void put(Object key, Object data);
	public ElementDescriptor<?> getDescriptor();
	public DslFactory getTragetOwnerFactory();
	public boolean isValid();
	public <T extends Trait> T asTrait(byte trait, boolean forceNew);
}
