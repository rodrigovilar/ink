package org.ink.core.vm.lang.internal;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.LifeCycleState;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.types.ObjectTypeMarker;

/**
 * @author Lior Schachter
 */
public interface MirrorAPI extends InkObjectState {

	public ObjectTypeMarker getObjectTypeMarker();

	public void setId(String id);

	public String getShortId();

	public String getNamespace();

	public InkObjectState getOwner();

	public void setOwner(InkObjectState owner);

	public PropertyMirror getDefiningProperty();

	public byte getDefiningPropertyIndex();

	public void setDefiningProperty(PropertyMirror propMirror, byte index);

	public boolean isAbstract();

	public void setAbstract(boolean isAbstract);

	public void init(InkClassState c);

	public void init(InkClassState c, DslFactory factory);

	public void boot(InkClassState c, DslFactory factory, InkObject behavior, Context context, Mirror mirror);

	public <T extends InkObjectState> T getSuper();

	public void setSuper(InkObjectState theSuperObject);

	public void setSuperId(String id);

	public PropertyMirror[] getPropertiesMirrors();

	public void afterPropertiesSet();

	public void setPropertyValue(String propertyName, Object value);

	public void setPropertyValue(byte index, Object value);

	public void insertValue(byte index, Object value);

	public Object getPropertyValue(String propertyName);

	public Object getPropertyValue(byte index);

	public Byte getPropertyIndex(String propertyName);

	public byte getPropertiesCount();

	public Scope getScope();

	public boolean hasBehaviorClass();

	public boolean hasStateClass();

	public boolean hasInterfaceClass();

	public void setScope(Scope scope);

	public void cacheBeahvior(InkObject behavior);

	public <B extends InkObject> B getCachedBehavior();

	public <T extends InkObjectState> T cloneState(boolean identicalTwin);

	public boolean isRoot();

	public void setRoot(boolean isRoot);

	public void cacheTrait(byte key, Trait trait);

	public <T extends Trait> T getCachedTrait(byte key);

	public boolean isCoreObject();

	public void setCoreObject(byte propCount, byte traitCount);

	public boolean isClass();

	public Object getRawValue(byte index);

	public void setRawValue(byte index, Object value);

	public boolean canHaveBehavior();

	public boolean isLoadOnStartup();

	public void setLoadOnStartUp(boolean loadOnStartUp);

	public Object get(Object key);

	public void put(Object key, Object data);

	public Object getPropertyStaticValue(byte index);

	public String getSuperId();

	public <T extends Trait> T asTrait(byte trait, boolean forceNew);
	
	public void setLifeCycleState(LifeCycleState toState);
	
	public LifeCycleState getLifeCycleState();

}
