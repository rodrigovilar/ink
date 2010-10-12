package org.ink.core.vm.factory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Struct;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitState;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.types.EnumTypeState;
import org.ink.core.vm.types.PrimitiveTypeMarker;

/**
 * @author Lior Schachter
 */
public interface DslFactory extends InkClass, Comparable<DslFactory>{
	
	public String getDslPackage();
	public String getJavaPackage();
	public String getNamespace();
	public <T extends InkObjectState> T newInstance(Class<T> stateClass);
	public <T extends InkObjectState> T newInstance(Class<T> stateClass, boolean initObjectId, boolean initDefaults);
	public <T extends Struct> T getStruct(String id);
	public <T extends InkObject> T getObject(String id);
	public <T extends InkClass> T getObject(Class<InkObjectState> stateClass);
	public <T extends InkObjectState> T getState(String id, boolean reportErrorIfNotExists);
	public <T extends InkObjectState> T getState(String id);
	public void register(InkObjectState state);
	public void register(InkObject o);
	public Class<InkObjectState> resolveDataClass(InkClassState cls);
	public Class<InkObject> resolveBehaviorClass(InkClassState cls);
	public Class<InkObject> resolveInterfaceClass(InkClassState cls);
	public Class<?> resolveEnumClass(EnumTypeState enumState);
	public <T extends InkObjectState> T newVanillaStateInstance(Class<T> stateClass);
	public InkObject newBehaviorInstance(TraitState state, InkObjectState targetState, boolean cacheResult, boolean forceNew);
	public <T extends InkObject> T newVanillaBehaviorInstance(Class<T> stateClass);
	public InkObject newBehaviorProxy(InkObject behaviorInstance, Class<?>[] types, Proxiability.Kind t);
	public InkObject newBehaviorProxy(InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex);
	public Mirror newMirrorProxy(Mirror behaviorInstance, Class<?>[] types, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex);
	public Struct newStructProxy(InkObjectState stateInstance, Class<?>[] type, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex);
	public boolean isNamespacesInScope(String ns);
	public Set<String> getScope();
	public Class<?> resolveCollectionClass(CollectionTypeMarker marker);
	public Class<?> resolvePrimitiveClass(PrimitiveTypeMarker marker);
	public Class<InkObjectState> resolveStructDataClass(InkClassState data);
	public void printElements(String toFile) throws IOException;
	public void registerTrait(TraitState o);
	public List<Trait> getDetachableTraits();
	public void validateAllElements(ValidationContext vc);
	public Context getAppContext();

}