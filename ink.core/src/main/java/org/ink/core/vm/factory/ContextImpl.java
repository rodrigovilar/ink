package org.ink.core.vm.factory;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Struct;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.traits.TraitImpl;

/**
 * @author Lior Schachter
 */
public class ContextImpl<S extends ContextState> extends TraitImpl<S> implements Context {

	@Override
	public String getNamespace() {
		return ((DslFactory)getTargetBehavior()).getNamespace();
	}

	@Override
	public <T extends InkObject> T getObject(String id) {
		return ((DslFactory)getTargetBehavior()).getObject(id);
	}

	@Override
	public <T extends InkClass> T getObject(Class<InkObjectState> stateClass) {
		return ((DslFactory)getTargetBehavior()).getObject(stateClass);
	}

	@Override
	public <T extends Struct> T getStruct(String id) {
		return ((DslFactory)getTargetBehavior()).getStruct(id);
	}

	@Override
	public <T extends InkObjectState> T newInstance(Class<T> stateClass) {
		return ((DslFactory)getTargetBehavior()).newInstance(stateClass);
	}

	@Override
	public <T extends InkObjectState> T newInstance(Class<T> stateClass,
			boolean initObjectId, boolean initDefaults) {
		return ((DslFactory)getTargetBehavior()).newInstance(stateClass, initObjectId, initDefaults);
	}

	@Override
	public void register(InkObjectState state) {
		((DslFactory)getTargetBehavior()).register(state);
	}

	@Override
	public void register(InkObject o) {
		((DslFactory)getTargetBehavior()).register(o);
	}

	@Override
	public DslFactory getFactory() {
		return (DslFactory) getTargetBehavior();
	}

	@Override
	public <T extends InkObjectState> T getState(String id){
		return getFactory().getState(id);
	}

	@Override
	public <T extends InkObjectState> T getState(InkObject object) {
		Mirror m = object.reflect();
		if(m.isRoot()){
			getState(m.getId());
		}
		return null;
	}

	@Override
	public <T extends InkObjectState> T getState(String id,
			boolean reportErrorIfNotExists) {
		return ((DslFactory)getTargetBehavior()).getState(id, reportErrorIfNotExists);
	}

}
