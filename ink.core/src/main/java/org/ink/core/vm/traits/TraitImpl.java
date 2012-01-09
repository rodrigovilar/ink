package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.ClassMirror;

/**
 * @author Lior Schachter
 */
public class TraitImpl<S extends TraitState> extends InkObjectImpl<S> implements Trait {

	private InkObjectState targetState = null;

	public void setTargetState(InkObjectState state) {
		this.targetState = state;
	}

	@SuppressWarnings("unchecked")
	protected <T extends InkObjectState> T getTargetState() {
		return (T) targetState;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InkObject> T getTargetBehavior() {
		return (T) getTargetState().getBehavior();
	}

	@Override
	public boolean isAcceptable(ClassMirror cls) {
		boolean result = false;
		if (getTargetState() != null) {
			result = cls.reflect().getId().equals(getTargetState().getMeta().reflect().getId());
		}
		if (!result && getState().getTargetLocator() != null) {
			result = getState().getTargetLocator().accept(cls);
		}
		return result;
	}

	/*
	 * @Override
	 * public Set<InkClass> getTragetClasses() {
	 * Set<InkClass> result = new HashSet<InkClass>();
	 * if(getTargetState()!=null){
	 * result.add(getTargetState().getMeta());
	 * }
	 * if(getState().getTragetLocator()!=null){
	 * result.addAll(getState().getTragetLocator().match());
	 * }
	 * return result;
	 * }
	 */

	@Override
	public void afterTargetSet() {
	}

}
