package org.ink.core.vm.mirror;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.factory.ElementDescriptor;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitImpl;
import org.ink.core.vm.types.ObjectTypeMarker;

/**
 * @author Lior Schachter
 */
public class MirrorImpl<S extends MirrorState> extends TraitImpl<S> implements Mirror {

	@Override
	public void afterTargetSet() {
		super.afterTargetSet();
	}

	@Override
	public String getShortId() {
		return ((MirrorAPI) getTargetState()).getShortId();
	}

	@Override
	public String getId() {
		return getTargetState().getId();
	}

	@Override
	public String getNamespace() {
		return ((MirrorAPI) getTargetState()).getNamespace();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ObjectEditor> T edit() {
		return (T) edit(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ObjectEditor> T edit(boolean transactional) {
		return (T) getState().getEditor().startEdit(getTargetState(), transactional);
	}

	@Override
	public ClassMirror getClassMirror() {
		return (ClassMirror) getTargetState().getMeta().reflect();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Mirror> M getRootOwner() {
		Mirror owner = getOwner();
		if (owner == null) {
			return (M) this;
		}
		return owner.getRootOwner();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Mirror> M getOwner() {
		InkObjectState owner = ((MirrorAPI) getTargetState()).getOwner();
		return owner == null ? null : (M) owner.reflect();
	}

	@Override
	public <M extends Mirror> M getSuper() {
		MirrorAPI superObject = ((MirrorAPI) getTargetState()).getSuper();
		if (superObject != null) {
			return superObject.reflect();
		}
		return null;
	}

	@Override
	public PropertyMirror[] getPropertiesMirrors() {
		return ((MirrorAPI) getTargetState()).getPropertiesMirrors();
	}

	@Override
	public PropertyMirror getPropertyMirror(byte index) {
		return ((MirrorAPI) getTargetState()).getPropertiesMirrors()[index];
	}

	private Byte getPropertyIndex(String name) {
		return ((MirrorAPI) getTargetState()).getPropertyIndex(name);
	}

	@Override
	public PropertyMirror getPropertyMirror(String name) {
		Byte index = getPropertyIndex(name);
		if (index != null) {
			return ((MirrorAPI) getTargetState()).getPropertiesMirrors()[index];
		}
		return null;
	}

	@Override
	public boolean isAbstract() {
		return ((MirrorAPI) getTargetState()).isAbstract();
	}

	@Override
	public Object getPropertyValue(String propertyName) {
		return ((MirrorAPI) getTargetState()).getPropertyValue(propertyName);
	}

	@Override
	public Object getPropertyValue(byte index) {
		return ((MirrorAPI) getTargetState()).getPropertyValue(index);
	}

	@Override
	public Object getPropertyStaticValue(byte index) {
		return ((MirrorAPI) getTargetState()).getPropertyStaticValue(index);
	}

	@Override
	public byte getPropertiesCount() {
		return ((MirrorAPI) getTargetState()).getPropertiesCount();
	}

	@Override
	public Scope getScope() {
		return ((MirrorAPI) getTargetState()).getScope();
	}

	@Override
	public <T extends InkObjectState> T cloneTargetState(boolean identicalTwin) {
		return ((MirrorAPI) getTargetState()).cloneState(identicalTwin);
	}

	@Override
	public <T extends InkObjectState> T cloneTargetState() {
		return ((MirrorAPI) getTargetState()).cloneState();
	}

	@Override
	public boolean isRoot() {
		return ((MirrorAPI) getTargetState()).isRoot();
	}

	@Override
	public boolean isClass() {
		return ((MirrorAPI) getTargetState()).isClass();
	}

	@Override
	public ObjectTypeMarker getObjectTypeMarker() {
		return ((MirrorAPI) getTargetState()).getObjectTypeMarker();
	}

	@Override
	public byte getDefiningPropertyIndex() {
		return ((MirrorAPI) getTargetState()).getDefiningPropertyIndex();
	}

	@Override
	public PropertyMirror getDefiningProperty() {
		return ((MirrorAPI) getTargetState()).getDefiningProperty();
	}

	@Override
	public boolean isInstanceOf(InkClass cls) {
		return getClassMirror().isSubClassOf(cls);
	}

	@Override
	public boolean isLoadOnStartup() {
		return ((MirrorAPI) getTargetState()).isLoadOnStartup();
	}

	@Override
	public boolean isCoreObject() {
		return ((MirrorAPI) getTargetState()).isCoreObject();
	}

	@Override
	public Object get(Object key) {
		return ((MirrorAPI) getTargetState()).get(key);
	}

	@Override
	public void put(Object key, Object data) {
		((MirrorAPI) getTargetState()).put(key, data);
	}

	@Override
	public ElementDescriptor<?> getDescriptor() {
		return getTargetState().getContext().getFactory().getDescriptor(getId());
	}

	@Override
	public DslFactory getTargetOwnerFactory() {
		return getTargetState().getContext().getFactory();
	}

	@Override
	public boolean isValid() {
		ElementDescriptor<?> desc = getDescriptor();
		if (desc != null) {
			return desc.isValid();
		}
		return true;
	}

	@Override
	public <T extends Trait> T asTrait(byte trait, boolean forceNew) {
		return ((MirrorAPI) getTargetState()).asTrait(trait, forceNew);
	}

	@Override
	public <T extends InkObjectState> T getTarget() {
		return getTargetState();
	}

	@Override
	public <B extends InkObject> B getCachedBehavior() {
		return ((MirrorAPI) getTargetState()).getCachedBehavior();
	}

}
