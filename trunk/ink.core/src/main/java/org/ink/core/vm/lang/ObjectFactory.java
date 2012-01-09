package org.ink.core.vm.lang;

import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.proxy.Proxiability;
import org.ink.core.vm.traits.TraitState;

/**
 * @author Lior Schachter
 */
public interface ObjectFactory extends InkObject {

	public void bind(ClassMirror cMirror);

	public String getNamespace();

	public boolean isPropertyFinal(byte index);

	public <T extends InkObjectState> T newInstance(DslFactory factory, InkClassState cls, boolean initObjectId, boolean initDefaults);

	public InkObject newBehviorInstance(InkObjectState state, boolean cacheResult, boolean forceNew);

	public InkObject newBehviorInstance(TraitState state, InkObjectState targetState, boolean cacheResult, boolean forceNew);

	public Struct newStructProxy(InkObjectState stateInstance, Class<?>[] type, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex);

	public InkObject newBehaviorProxy(InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t);

	public InkObject newBehaviorProxy(InkObject behaviorInstance, InkObjectState state, Class<?>[] types, Proxiability.Kind t, InkObjectState owner, PropertyMirror definingProperty, byte definingPropertyIndex);
}
