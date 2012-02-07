package org.ink.core.vm.mirror;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.ElementDescriptor;
import org.ink.core.vm.lang.ComponentType;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.exceptions.InvalidPathException;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.types.ObjectTypeMarker;

/**
 * The <code>Mirror</code> interface represents the <code>reflection</code> trait of <code>InkObject</code>. It contains reflection-related methods, such as
 * introspection of properties, classes, meta-classes etc.
 * <br/>
 * A <code>Mirror</code> instance is obtained by calling the {@link Proxiable#reflect()} method.
 * @author Lior Schachter
 */
public interface Mirror extends Trait {

	/**
	 * Retrieves the object type of the target (object, class, meta-class, enum).
	 * @return the object type of the target.
	 */
	public ObjectTypeMarker getObjectTypeMarker();

	/**
	 * Retrieves the object ID of the target.
	 * @return the object ID of the target.
	 * @see InkObjectState#getId()
	 */
	public String getId();

	/**
	 * Retrieves the short ID (i.e., not including the namespace) of the target.
	 * @return the short ID (i.e., not including the namespace) of the target.
	 */
	public String getShortId();

	/**
	 * Retrieves the namespace of the target.
	 * @return the namespace of the target.
	 */
	public String getNamespace();

	/**
	 * Begins a non-transactional edit operation on the target.
	 * @return an {@link ObjectEditor} initialized with this <code>Mirror</code>'s target.
	 */
	public <T extends ObjectEditor> T edit();

	/**
	 * Begins an edit operation on the target, optionally making that operation transactional. A transactional edit operation makes the
	 * changes to an isolated copy of the object, and merges it into the Ink VM only when the transaction is ended. A non-transactional
	 * edit operation makes the changes directly to the object in the Ink VM.
	 * @param transactional whether the edit operation should be isolated in a transaction.
	 * @return an {@link ObjectEditor} initialized with this <code>Mirror</code>'s target.
	 */
	public <T extends ObjectEditor> T edit(boolean transactional);

	/**
	 * Retrieves the <code>Mirror</code> of the target's class.
	 * @return the <code>Mirror</code> of the target's class.
	 */
	public ClassMirror getClassMirror();

	/**
	 * Retrieves the target state object.
	 * @return the target state object.
	 */
	public <T extends InkObjectState> T getTarget();

	/**
	 * Retrieves the <code>Mirror</code> of the target's owner (i.e., the object containing the target).
	 * @return the <code>Mirror</code> of the target's owner.
	 */
	public <M extends Mirror> M getOwner();

	/**
	 * Retrieves the <code>Mirror</code> of the target's root owner (i.e., the top-level object containing the target).
	 * @return the <code>Mirror</code> of the target's root owner.
	 */
	public <M extends Mirror> M getRootOwner();

	/**
	 * Retrieves the <code>Mirror</code> of the target's super-object (i.e., the object which the target extends).
	 * @return the <code>Mirror</code> of the target's super-object.
	 */
	public <M extends Mirror> M getSuper();

	/**
	 * Retrieves the target's <code>abstract</code> attribute.
	 * @return the target's <code>abstract</code> attribute.
	 */
	public boolean isAbstract();

	/**
	 * Retrieves the <code>Mirror</code> of the definition of the required property on the target.
	 * @param index the index of the required property.
	 * @return the <code>Mirror</code> of the definition of the required property on the target.
	 * @throws ArrayIndexOutOfBoundsException if the index is invalid.
	 */
	public PropertyMirror getPropertyMirror(byte index);

	/**
	 * Retrieves the <code>Mirror</code> of the definition of the required property on the target.
	 * @param name the name of the required property.
	 * @return the <code>Mirror</code> of the definition of the required property on the target, or <code>null</code> if the name is not found.
	 */
	public PropertyMirror getPropertyMirror(String name);

	/**
	 * Retrieves the <code>Mirror</code>s of the definition of all the properties on the target.
	 * @return the <code>Mirror</code>s of the definition of all the properties on the target.
	 */
	public PropertyMirror[] getPropertiesMirrors();

	/**
	 * Retrieves the value of a property of the target.
	 * @param propertyName the name of the required property.
	 * @return the value of the required property.
	 * @throws IllegalArgumentException if the property name is not found.
	 */
	public Object getPropertyValue(String propertyName);

	/**
	 * Retrieves the value of a property of the target.
	 * @param index the index of the required property.
	 * @return the value of the required property.
	 * @throws ArrayIndexOutOfBoundsException if the index is invalid.
	 */
	public Object getPropertyValue(byte index);

	public Object getPropertyStaticValue(byte index);

	/**
	 * Retrieves the number of properties the target has.
	 * @return the number of properties the target has.
	 */
	public byte getPropertiesCount();

	/**
	 * Retrieves the visibility scope of the target.
	 * @return the visibility scope of the target.
	 */
	public Scope getScope();

	/**
	 * Retrieves the <code>Mirror</code> of the definition of the property that contains the target. The property belongs to the class of the target's owner.
	 * @return the <code>Mirror</code> of the definition of the property that contains the target.
	 */
	public PropertyMirror getDefiningProperty();

	/**
	 * Retrieves the index of the property that contains the target. The property belongs to the class of the target's owner.
	 * @return the index of the property that contains the target.
	 */
	public byte getDefiningPropertyIndex();

	/**
	 * Creates a deep clone of the target object.
	 * @return a deep clone of the target object.
	 * @see InkObjectState#cloneState()
	 */
	public <T extends InkObjectState> T cloneTargetState();

	/**
	 * Creates a deep clone of the target object, optionally preserving object IDs of the target and all the contained Ink objects.
	 * @param identicalTwin whether IDs should be preserved.
	 * @return a deep clone of the target object.
	 */
	public <T extends InkObjectState> T cloneTargetState(boolean identicalTwin);

	/**
	 * Returns <code>true</code> if the component type of the target's class is defined as Root.
	 * @return <code>true</code> if the component type of the target's class is defined as Root.
	 * @see ComponentType
	 */
	public boolean isRoot();

	/**
	 * Returns <code>true</code> if the target is a class or meta-class.
	 * @return <code>true</code> if the target is a class or meta-class.
	 */
	public boolean isClass();

	/**
	 * Returns <code>true</code> if the target is an instance of the required Ink class.
	 * @param cls the Ink class to test.
	 * @return <code>true</code> if the target is an instance of the required Ink class.
	 */
	public boolean isInstanceOf(InkClass cls);

	/**
	 * Returns <code>true</code> if the target is marked to be eagerly loaded on the startup of Ink VM.
	 * @return <code>true</code> if the target is marked to be eagerly loaded on the startup of Ink VM.
	 */
	public boolean isLoadOnStartup();

	/**
	 * Returns <code>true</code> if the target belongs to the core of the Ink VM.
	 * @return <code>true</code> if the target belongs to the core of the Ink VM.
	 */
	public boolean isCoreObject();

	/**
	 * Retrieves a value from the target's inner cache. The cache is stored on the target object, and an object placed in the cache is not guaranteed
	 * to be there indefinitely.
	 * @param key the key whose associated value is to be returned.
	 * @return the value to which the specified key is mapped, or <code>null</code> if this map contains no mapping for the key.
	 */
	public Object get(Object key);

	/**
	 * Stores a value in the target's inner cache. The cache is stored on the target object, and an object placed in the cache is not guaranteed
	 * to be there indefinitely.
	 * @param key the key with which the specified value is to be associated.
	 * @param data the value to be associated with the specified key.
	 */
	public void put(Object key, Object data);

	/**
	 * Retrieves the element descriptor of the target.
	 * @return the element descriptor of the target.
	 */
	public ElementDescriptor<?> getDescriptor();

	/**
	 * Retrieves the {@link DslFactory} which loaded the target object.
	 * @return the {@link DslFactory} which loaded the target object.
	 */
	public DslFactory getTargetOwnerFactory();

	/**
	 * Returns <code>false</code> if the target couldn't be instantiated successfully by the Ink VM.
	 * @return <code>false</code> if the target couldn't be instantiated successfully by the Ink VM.
	 */
	public boolean isValid();

	/**
	 * Cast the target object as a trait, allowing to force the creation and initialization of a new trait object.
	 * @param trait an absolute index of the trait.
	 * @param forceNew whether a new trait object should be created even if such a trait was already initialized.
	 * @return the target object cast to the required trait.
	 * @see Proxiable#asTrait(byte)
	 * @throws ArrayIndexOutOfBoundsException if the index is invalid.
	 */
	public <T extends Trait> T asTrait(byte trait, boolean forceNew);
	
	/**
	 * Retrieves the cached behavior instance of this state, resulting null if the method {@link InkObjectState#getBehavior()} not called on this state.  
	 * @return the behavior instances.
	 */
	public <B extends InkObject> B getCachedBehavior();

	/**
	 * Allows fetching a value from an object graph whose root is this object, using dot notation path.  
	 * @param path - the path to the value.  Elements in the path are property names.
	 * @return {@link Object} - The value
	 * @throws InvalidPathException : when path syntax error occurs 
	 */
	public Object getValueByPath(String path) throws InvalidPathException;
}
