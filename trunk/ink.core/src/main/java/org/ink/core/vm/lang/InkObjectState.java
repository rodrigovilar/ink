package org.ink.core.vm.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ink.core.vm.constraints.Constraints;
import org.ink.core.vm.constraints.InstanceValidator;
import org.ink.core.vm.constraints.PropertyValueValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.Validator;
import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.lang.internal.ClassMirrorAPI;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.lang.store.DataHolder;
import org.ink.core.vm.lang.store.DefaultDataHolder;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.StructClassMirror;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;
import org.ink.core.vm.types.CollectionTypeMarker;
import org.ink.core.vm.types.ObjectTypeMarker;
import org.ink.core.vm.utils.CoreUtils;
import org.ink.core.vm.utils.InkNotations;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;

/**
 * The interface of the base Ink state object. Every state object in Ink implements this interface (explicitly or implicitly).
 * 
 * @see InkObject
 * @see InkObjectImpl
 * 
 * @author Lior Schachter
 */
public interface InkObjectState extends Proxiable, Cloneable, Serializable {

	/**
	 * The {@link Context} of this state object. This is the <code>context</code> trait of the {@link DslFactory} that loaded this object.
	 * @return the {@link Context} of this state object.
	 */
	public Context getContext();

	/**
	 * Retrieves the unique ID of this state object.
	 * @return the unique ID of this state object.
	 */
	public String getId();

	/**
	 * Retrieves the behavior object of this state object. If the Ink class is defined with Java mapping "No Java", "State-Interface" or "State only", the behavior is inherited,
	 * i.e. the behavior object will be an instance of the behavior class of a superclass.
	 * The behavior object will be created and initialized on the first invocation of this method.
	 * @return the state object of this behavior object.
	 */
	public <B extends InkObject> B getBehavior();

	@Override
	public <T extends Trait> T asTrait(String role);

	/**
	 * Cast this object as a trait. A trait is a part of an object's personality.
	 * @param traitClass the class of the trait to which this object should be cast.
	 * @return this object cast to the required trait, or <code>null</code> if the trait class wasn't found.
	 */
	public <T extends Trait> T asTrait(TraitClass traitClass);

	/**
	 * Creates a deep clone of this object. The clone operation traverses the state object and creates a clone of every mutable
	 * object it encounters, such as inner state objects and collections. (Primitive values are immutable and therefore are shallow-copied.)
	 * <br/>
	 * Object IDs are not cloned. The cloned state object and all its inner objects have unique IDs. Behavior objects are not created during this
	 * operation.
	 * @return a deep clone of this state object.
	 */
	public <T extends InkObjectState> T cloneState();

	/**
	 * Validates this object by executing the validators from the <code>constraints</code> trait relevant to run-time system state.
	 * Equivalent to calling <code>validate(context, SystemState.Run_Time)</code>.
	 * The messages reported during validation are collected into the context object.
	 * <br/>
	 * @param context a {@link ValidationContext} object into which the validation messages are collected.
	 * @return <code>true</code> if no validation errors were encountered, <code>false</code> otherwise.
	 * @see Validator
	 * @see InstanceValidator#validate(InkObjectState, org.ink.core.vm.mirror.Mirror, ValidationContext, SystemState)
	 * @see PropertyValueValidator#validate(Property, Object, InkObjectState, ValidationContext, SystemState)
	 */
	public boolean validate(ValidationContext context);

	/**
	 * Validates this object by executing the validators from the <code>constraints</code> trait relevant to the required system state.
	 * The messages reported during validation are collected into the context object.
	 * <br/>
	 * @param context a {@link ValidationContext} object into which the validation messages are collected.
	 * @param systemState denotes whether design-time or run-time validators should be invoked.
	 * @return <code>true</code> if no validation errors were encountered, <code>false</code> otherwise.
	 * @see Validator
	 * @see InstanceValidator#validate(InkObjectState, org.ink.core.vm.mirror.Mirror, ValidationContext, SystemState)
	 * @see PropertyValueValidator#validate(Property, Object, InkObjectState, ValidationContext, SystemState)
	 */
	public boolean validate(ValidationContext context, SystemState systemState);

	/**
	 * Index of the <code>reflection</code> trait of this class.
	 */
	public static final byte t_reflection = 0;

	/**
	 * Index of the <code>constraints</code> trait of this class.
	 */
	public static final byte t_constraints = 1;

	/**
	 * The base Ink state inner class. Every state object in Ink extends this class.
	 */
	public class Data implements MirrorAPI {

		private static int cache_max_entries = 100;

		// static state
		private String id = null;
		private String superId = null;
		private boolean loadOnStartUp = false;
		private InkObjectState mySuper;
		private Scope scope;
		private boolean isAbstract;
		private boolean isRoot = false;
		private boolean isCoreObject = false;
		protected DslFactory myFactory;
		private DataHolder myData;

		// runtime state
		protected PropertyMirror[] propsMirrors;
		private InkObjectState owner = null;
		private Trait[] traitsCache = null;
		private PropertyMirror definingProperty = null;
		private byte definingPropertyIndex = -1;
		protected Map<String, Byte> propertiesIndexes;
		protected InkObject behavior;
		protected ClassMirrorAPI myClass;
		private byte[] realPropertiesIndex;
		private Map<Object, Object> cache = null;

		@Override
		public Context getContext() {
			return myFactory.getAppContext();
		}

		@Override
		public void init(InkClassState c) {
			init(c, InkVM.instance().getFactory());
		}

		@Override
		public void init(InkClassState c, DslFactory factory) {
			this.myClass = (ClassMirrorAPI) c;
			this.propertiesIndexes = myClass.getClassPropertiesIndexes();
			this.propsMirrors = myClass.getClassPropertiesMirrors();
			this.myFactory = factory;
			this.myData = new DefaultDataHolder((byte) propsMirrors.length);
			this.traitsCache = new Trait[myClass.getTraitsCount()];
			this.realPropertiesIndex = myClass.getRealPropertiesIndex();
		}

		@Override
		public boolean isProxied() {
			return false;
		}

		@Override
		public void boot(InkClassState c, DslFactory factory, InkObject behavior, Context context, Mirror mirror) {
			this.myClass = (ClassMirrorAPI) c;
			this.propertiesIndexes = myClass.getClassPropertiesIndexes();
			this.propsMirrors = myClass.getClassPropertiesMirrors();
			this.myFactory = factory;
			this.traitsCache[t_reflection] = mirror;
			if (behavior != null) {
				this.behavior = behavior;
				((InkObjectImpl<?>) behavior).setState(this, context);
			}
		}

		@Override
		public <T extends InkObjectState> T cloneState() {
			return cloneState(false);
		}

		private void cloneData(MirrorAPI destination, boolean identicalTwin) {
			Object currentValue = null;
			Object clonedValue = null;
			for (PropertyMirror t : propsMirrors) {
				if (t.isMutable()) {
					currentValue = myData.getValue(t.getIndex());
					if (currentValue != null) {
						clonedValue = CoreUtils.cloneOneValue(t, currentValue, identicalTwin);
					} else {
						clonedValue = null;
					}
					destination.setPropertyValue(t.getIndex(), clonedValue);
				}
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T extends InkObjectState> T cloneState(boolean identicalTwin) {
			MirrorAPI result = null;
			try {
				boolean initObjectId = !identicalTwin & isRoot;
				result = (MirrorAPI) getMeta().newInstance(getContext(), initObjectId, false);
				if (!initObjectId) {
					result.setId(id);
				}
				cloneData(result, identicalTwin);
				if (identicalTwin) {
					result.setId(this.getId());
				}
				result.setSuper(this.getSuper());
				result.setRoot(isRoot);
				result.setAbstract(isAbstract);
				if (this.isAbstract) {
					result.setAbstract(true);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return (T) result;
		}

		@Override
		public boolean isClass() {
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public final <M extends Mirror> M reflect() {
			return (M) asTrait(t_reflection);
		}

		@Override
		public Object get(Object key) {
			if (cache == null) {
				return null;
			}
			Object result = null;
			result = cache.get(key);
			return result;
		}

		@Override
		public void put(Object key, Object data) {
			if (cache == null) {
				cache = new ConcurrentHashMap<Object, Object>(8);
			} else if (cache.size() >= cache_max_entries && !(cache.containsKey(key))) {
				while (cache.size() >= cache_max_entries) {
					try {
						Object keyToRemove = cache.keySet().iterator().next();
						cache.remove(keyToRemove);
					} catch (Exception e) {
					}
				}
			}
			cache.put(key, data);
		}

		@Override
		public final void cacheTrait(byte key, Trait trait) {
			traitsCache[key] = trait;
		}

		@SuppressWarnings("unchecked")
		@Override
		public final <T extends Trait> T getCachedTrait(byte key) {
			return (T) traitsCache[key];
		}

		@Override
		@SuppressWarnings("unchecked")
		public <B extends InkObject> B getBehavior() {
			if (behavior == null) {
				return (B) myClass.getFactory().newBehviorInstance(this, myClass.getCanCacheBehaviorInstance(), false);
			}
			return (B) behavior;
		}

		@Override
		public final <T extends Trait> T asTrait(byte trait) {
			return asTrait(trait, false);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Trait> T asTrait(byte trait, boolean forceNew) {
			Trait result = null;
			if (forceNew) {
				traitsCache[trait] = null;
			} else {
				result = traitsCache[trait];
			}
			if (result == null) {
				result = myClass.getPersonality().adapt(trait, this, (ClassMirror) getMeta().reflect());
			}
			return (T) result;
		}

		/**
		 * Returns a string representation of this object in the Ink source format.
		 */
		@Override
		public String toString() {
			StringBuilder temp = new StringBuilder(1000);
			CoreUtils.toString(this, myClass, temp);
			StringBuilder result = new StringBuilder(temp.length() + 100);
			int count = 0;
			char c;
			for (int i = 0; i < temp.length(); i++) {
				c = temp.charAt(i);
				result.append(c);
				if (c == '{') {
					count++;
				} else if (c == '}') {
					count--;
				} else if (c == '\n') {
					if (count > 0) {
						if (temp.charAt(i + 1) == '}') {
							if (count >= 2) {
								result.append(CoreUtils.TAB);
							}
						} else {
							result.append(CoreUtils.TAB);
						}
					}
				}
			}
			return result.toString();
		}

		@Override
		public String getShortId() {
			if (id == null) {
				return null;
			}
			return id.substring(id.indexOf(InkNotations.Path_Syntax.NAMESPACE_DELIMITER_C) + 1, id.length());
		}

		/**
		 * Returns <code>true</code> if <code>obj</code> is an Ink state object whose ID is equal to this object's ID, <code>false</code> otherwise.
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj != null && id != null && obj instanceof InkObjectState) {
				return id.equals(((InkObjectState) obj).getId());
			}
			return false;
		}

		/**
		 * Returns the hash code of this object's ID.
		 */
		@Override
		public int hashCode() {
			return id == null ? 0 : id.hashCode();
		}

		@Override
		public final void insertValue(byte index, Object value) {
			if (value != null) {
				value = prepareValueToSet(propsMirrors[index], value, index);
			}
			myData.setValue(index, value);
		}

		protected final void setValue(byte index, Object value) {
			setPropertyValue(realPropertiesIndex[index], value);
		}

		@Override
		public boolean canHaveBehavior() {
			return true;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Object prepareValueToReturn(PropertyMirror propMirror, Object value, byte propIndex) {
			DataTypeMarker typeMarker = propMirror.getTypeMarker();
			Object result = value;
			switch (typeMarker) {
			case Class:
				if (!((Proxiable) value).isProxied() && ((MirrorAPI) result).canHaveBehavior()) {
					if (((MirrorAPI) result).isRoot()) {
						InkObject existingBehavior = ((MirrorAPI) result).getBehavior();
						Proxiability.Kind proxyType = Proxiability.Kind.BEHAVIOR_OWNER;
						if (existingBehavior.isProxied()) {
							existingBehavior = ((Proxiability) existingBehavior).getVanillaBehavior();
							proxyType = Proxiability.Kind.BEHAVIOR_BOTH;
						}
						result = myClass.getFactory().newBehaviorProxy(existingBehavior, (MirrorAPI) result, ((ClassMirror) ((InkObjectState) result).getMeta().reflect()).getBehaviorProxyInterfaces(), proxyType, this, propMirror, propIndex);
					} else {
						result = ((InkObjectState) result).getBehavior();
					}
				}
				break;
			case Collection:
				CollectionTypeMarker cMarker = ((CollectionPropertyMirror) propMirror).getCollectionTypeMarker();
				switch (cMarker) {
				case List:
					List<?> col = (List<?>) value;
					result = new ArrayList();
					PropertyMirror innerType = ((ListPropertyMirror) propMirror).getItemMirror();
					if (innerType.isValueDrillable()) {
						for (Object o : col) {
							((List) result).add(prepareValueToReturn(innerType, o, propIndex));
						}
					} else {
						result = col;
					}
					break;
				case Map:
					Map<?, ?> map = (Map<?, ?>) value;
					result = map;
					if (!map.isEmpty()) {
						PropertyMirror innerKeyType = ((MapPropertyMirror) propMirror).getKeyMirror();
						innerType = ((MapPropertyMirror) propMirror).getValueMirror();
						boolean isKeyDrillable = innerKeyType.isValueDrillable();
						boolean isValueDrillable = innerType.isValueDrillable();
						if (isKeyDrillable || isValueDrillable) {
							Object mapValue;
							result = ((MapPropertyMirror) propMirror).getNewInstance();
							for (Object mapKey : map.keySet()) {
								mapValue = map.get(mapKey);
								if (isKeyDrillable && mapKey != null) {
									mapKey = prepareValueToReturn(innerKeyType, mapKey, propIndex);
								}
								if (isValueDrillable && mapValue != null) {
									mapValue = prepareValueToReturn(innerType, mapValue, propIndex);
								}
								((Map) result).put(mapKey, mapValue);
							}
						}
					}
					break;
				}
				break;
			}
			return result;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Object prepareValueToSet(PropertyMirror propMirror, Object value, byte propIndex) {
			DataTypeMarker typeMarker = propMirror.getTypeMarker();
			Object result = value;
			switch (typeMarker) {
			case Class:
				if (((Proxiable) value).getObjectKind() == Kind.Behavior) {
					if (((Proxiable) value).isProxied()) {
						result = value = ((Proxiability) value).getVanillaState();
					} else {
						result = value = ((InkObjectImpl) value).getState();
					}
				}
				MirrorAPI state = (MirrorAPI) value;
				if (state.isRoot()) {
					if (state.canHaveBehavior()) {
						if (((ClassMirror) state.getMeta().reflect()).canCacheBehaviorInstance()) {
							InkObject existingBehavior = ((MirrorAPI) result).getBehavior();
							Proxiability.Kind proxyType = Proxiability.Kind.BEHAVIOR_OWNER;
							if (existingBehavior.isProxied()) {
								existingBehavior = ((Proxiability) existingBehavior).getVanillaState().getBehavior();
								proxyType = Proxiability.Kind.BEHAVIOR_BOTH;
							}
							result = myClass.getFactory().newBehaviorProxy(existingBehavior, (MirrorAPI) result, ((ClassMirror) state.getMeta().reflect()).getBehaviorProxyInterfaces(), proxyType, this, propMirror, propIndex);
						}
					} else {
						if (state.isProxied()) {
							state = ((Proxiability) result).getVanillaState();
						}
						result = myClass.getFactory().newStructProxy(state, ((StructClassMirror) state.getMeta().reflect()).getStateProxyInterfaces(), this, propMirror, propIndex);
					}
				} else {
					state.setDefiningProperty(propMirror, propIndex);
					state.setOwner(this);
				}
				break;
			case Collection:
				CollectionTypeMarker cMarker = ((CollectionPropertyMirror) propMirror).getCollectionTypeMarker();
				switch (cMarker) {
				case List:
					List col = (List) value;
					PropertyMirror innerType = ((ListPropertyMirror) propMirror).getItemMirror();
					if (innerType.isValueDrillable()) {
						Object innerO;
						Object temp;
						for (int i = 0; i < col.size(); i++) {
							innerO = col.get(i);
							temp = prepareValueToSet(innerType, innerO, propIndex);
							if (innerO != temp) {
								col.set(i, temp);
							}
						}
					}
					break;
				case Map:
					Map<Object, Object> map = (Map<Object, Object>) value;
					PropertyMirror innerKeyType = ((MapPropertyMirror) propMirror).getKeyMirror();
					innerType = ((MapPropertyMirror) propMirror).getValueMirror();
					boolean isKeyDrillable = innerKeyType.isValueDrillable();
					boolean isValueDrillable = innerType.isValueDrillable();
					if (isKeyDrillable || isValueDrillable) {
						Object mapKey;
						Object mapValue;
						boolean changed = false;
						for (Map.Entry en : map.entrySet()) {
							if (isKeyDrillable) {
								mapKey = prepareValueToSet(innerKeyType, en.getKey(), propIndex);
								changed = true;
							} else {
								mapKey = en.getKey();
							}
							if (isValueDrillable) {
								mapValue = prepareValueToSet(innerType, en.getValue(), propIndex);
								changed = true;
							} else {
								mapValue = en.getValue();
							}
							if (changed) {
								map.put(mapKey, mapValue);
							}
							changed = false;
						}
					}
					break;
				}
				break;
			}
			return result;
		}

		@Override
		public final Object getRawValue(byte index) {
			return myData.getValue(index);
		}

		@Override
		public final void setRawValue(byte index, Object value) {
			myData.setValue(index, value);
		}

		protected final Object getValue(byte index) {
			return getPropertyValue(realPropertiesIndex[index]);
		}

		@Override
		public String getNamespace() {
			return myFactory.getNamespace();
		}

		@SuppressWarnings("unchecked")
		@Override
		public final <M extends InkClass> M getMeta() {
			return (M) myClass.getBehavior();
		}

		@Override
		public final String getId() {
			return id;
		}

		@Override
		public final void setId(String id) {
			this.id = id;
		}

		@Override
		public Scope getScope() {
			return scope;
		}

		@Override
		public void setScope(Scope scope) {
			this.scope = scope;
		}

		@Override
		public final InkObjectState getOwner() {
			return owner;
		}

		@Override
		public final void setOwner(InkObjectState owner) {
			this.owner = owner;
		}

		@Override
		public final Object getPropertyValue(String propertyName) {
			Byte loc = propertiesIndexes.get(propertyName);
			if (loc == null) {
				throw new IllegalArgumentException("The property '" + propertyName + "' does not exist on instance '" + getId() + "' of type '" + myClass.getId() + "'.");
			}
			return getPropertyValue(loc);
		}

		@Override
		public Byte getPropertyIndex(String propertyName) {
			return propertiesIndexes.get(propertyName);
		}

		@Override
		public final Object getPropertyValue(byte index) {
			Object value = myData.getValue(index);
			PropertyMirror mirror = propsMirrors[index];
			if (value != null) {
				value = prepareValueToReturn(mirror, value, index);
			}
			return mirror.produceValue(this, value);
		}

		@Override
		public final Object getPropertyStaticValue(byte index) {
			Object value = myData.getValue(index);
			PropertyMirror mirror = propsMirrors[index];
			if (value != null) {
				value = prepareValueToReturn(mirror, value, index);
			}
			return value;
		}

		@Override
		public final void setPropertyValue(String propertyName, Object value) {
			Byte loc = propertiesIndexes.get(propertyName);
			if (loc == null) {
				throw new IllegalArgumentException("The property '" + propertyName + "' does not exist on instance '" + getId() + "' of type '" + myClass.getId() + "'.");
			}
			setPropertyValue(loc, value);
		}

		@Override
		public final void setPropertyValue(byte index, Object value) {
			if (!propsMirrors[index].hasStaticValue()) {
				throw new CoreException("Property '" + propsMirrors[index].getName() + "' of class '" + myClass.getId() + "' is a calculated property.");
			} else if (!propsMirrors[index].isMutable()) {
				throw new CoreException("Property '" + propsMirrors[index].getName() + "' of class '" + myClass.getId() + "' is read only.");
			}
			insertValue(index, value);
		}

		@Override
		public final boolean isAbstract() {
			return isAbstract;
		}

		@Override
		public final void setAbstract(boolean isAbstract) {
			this.isAbstract = isAbstract;
		}

		@Override
		public final PropertyMirror[] getPropertiesMirrors() {
			return propsMirrors;
		}

		@SuppressWarnings("unchecked")
		@Override
		public final <T extends InkObjectState> T getSuper() {
			return (T) mySuper;
		}

		@Override
		public final void setSuper(InkObjectState theSuperObject) {
			this.mySuper = theSuperObject;
			if (theSuperObject != null) {
				this.superId = theSuperObject.getId();
			}
		}

		@Override
		public void setSuperId(String id) {
			this.superId = id;
		}

		@Override
		public String getSuperId() {
			return superId;
		}

		@Override
		public void afterPropertiesSet() {
			if (this.mySuper == null && this.superId != null) {
				this.mySuper = myFactory.getState(superId);
				// TODO add exception if mySuper still equals null
			}
			if (scope == null) {
				scope = Scope.all;
			}
			if (traitsCache != null) {
				for (Trait t : traitsCache) {
					if (t != null) {
						t.afterTargetSet();
					}
				}
			}
			if (behavior != null) {
				behavior.afterStateSet();
			}
		}

		@Override
		public byte getPropertiesCount() {
			return (byte) propsMirrors.length;
		}

		@Override
		public void cacheBeahvior(InkObject behavior) {
			this.behavior = behavior;
		}

		@Override
		public final InkObject getCachedBehavior() {
			return behavior;
		}

		@Override
		public final byte getDefiningPropertyIndex() {
			return definingPropertyIndex;
		}

		@Override
		public final void setDefiningProperty(PropertyMirror propMirror, byte index) {
			this.definingProperty = propMirror;
			this.definingPropertyIndex = index;
		}

		@Override
		public final boolean isRoot() {
			return isRoot;
		}

		@Override
		public final void setRoot(boolean isRoot) {
			this.isRoot = isRoot;
		}

		@Override
		public boolean hasBehaviorClass() {
			return myClass.getJavaMapping().hasBehavior();
		}

		@Override
		public boolean hasStateClass() {
			return myClass.getJavaMapping().hasState();
		}

		@Override
		public boolean hasInterfaceClass() {
			return myClass.getJavaMapping().hasInterface();
		}

		@Override
		public final boolean isCoreObject() {
			return isCoreObject;
		}

		@Override
		public final void setCoreObject(byte propCount, byte traitCount) {
			this.isCoreObject = true;
			this.propsMirrors = new PropertyMirror[0];
			this.traitsCache = new Trait[traitCount];
			this.myData = new DefaultDataHolder(propCount);
			this.realPropertiesIndex = new byte[propCount];
			for (byte i = 0; i < realPropertiesIndex.length; i++) {
				realPropertiesIndex[i] = i;
			}
		}

		@Override
		public ObjectTypeMarker getObjectTypeMarker() {
			return ObjectTypeMarker.Object;
		}

		@Override
		public final PropertyMirror getDefiningProperty() {
			return this.definingProperty;
		}

		@Override
		public <T extends Trait> T asTrait(String role) {
			Byte index = myClass.getTraitIndex(role);
			if (index != null) {
				return asTrait(index);
			}
			return null;
		}

		@Override
		public <T extends Trait> T asTrait(TraitClass traitClass) {
			Byte index = myClass.getTraitIndex(traitClass);
			if (index != null) {
				return asTrait(index);
			}
			return null;
		}

		@Override
		public boolean isLoadOnStartup() {
			return loadOnStartUp;
		}

		@Override
		public void setLoadOnStartUp(boolean loadOnStartUp) {
			this.loadOnStartUp = loadOnStartUp;
		}

		/**
		 * Returns <code>Kind.State</code>.
		 */
		@Override
		public Kind getObjectKind() {
			return Proxiable.Kind.State;
		}

		@Override
		public boolean validate(ValidationContext context) {
			return validate(context, SystemState.Run_Time);
		}

		@Override
		public boolean validate(ValidationContext context, SystemState systemState) {
			Constraints constraints = asTrait(InkObjectState.t_constraints);
			Mirror mirror = reflect();
			return constraints.validateTarget(mirror.getSuper(), context, systemState);
		}

	}

}