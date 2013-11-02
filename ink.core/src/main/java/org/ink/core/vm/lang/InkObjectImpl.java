package org.ink.core.vm.lang;

import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.traits.Trait;

/**
 * The class of the base Ink behavior object. Every behavior class in Ink
 * extends this class (explicitly or implicitly).
 * 
 * @see InkObject
 * @see InkObjectState
 * 
 * @author Lior Schachter
 */
public class InkObjectImpl<S extends InkObjectState> implements InkObject {
	private S state = null;
	private Context context = null;

	/**
	 * This method is invoked by the Ink VM in order to inject the behavior
	 * object with its state and context objects. <br/>
	 * <b>This method is intended for internal use by the Ink VM only.</b>
	 */
	@SuppressWarnings("unchecked")
	void setState(InkObjectState state, Context context) {
		this.state = (S) state;
		this.context = context;
	}

	/**
	 * Retrieves the state object of this behavior object. The state object
	 * contains the values of the properties defined in this object's class. The
	 * state object is guaranteed to be non-null. <br/>
	 * The behavior object should never expose its state object to external
	 * clients. The state should be accessible to its behavior only.
	 * 
	 * @return the state object of this behavior object.
	 */
	protected S getState() {
		return state;
	}

	/**
	 * The {@link Context} of this behavior object. This is the
	 * <code>context</code> trait of the {@link DslFactory} that loaded this
	 * object.
	 * 
	 * @return the {@link Context} of this behavior object.
	 */
	protected Context getContext() {
		return context;
	}

	@Override
	public final <M extends Mirror> M reflect() {
		return state.reflect();
	}

	@Override
	public <T extends Trait> T asTrait(byte aspect) {
		return state.asTrait(aspect);
	}

	@Override
	public <M extends InkClass> M getMeta() {
		return state.getMeta();
	}

	@Override
	public void afterStateSet() {
	}

	@Override
	public <T extends InkObjectState> T cloneState() {
		return getState().cloneState();
	}

	@Override
	public boolean isProxied() {
		return false;
	}

	/**
	 * Returns a string representation of this object's state in the Ink source
	 * format.
	 */
	@Override
	public String toString() {
		return state.toString();
	}

	/**
	 * Returns <code>true</code> if <code>obj</code> is an Ink behavior object
	 * whose ID is equal to this object's ID, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		String id = state.getId();
		if (obj != null && id != null && obj instanceof InkObject) {
			return id.equals(((InkObject) obj).reflect().getId());
		}
		return false;
	}

	/**
	 * Returns the hash code of this object's ID.
	 */
	@Override
	public int hashCode() {
		String id = state.getId();
		return id == null ? super.hashCode() : id.hashCode();
	}

	/**
	 * Returns <code>Kind.Behavior</code>.
	 */
	@Override
	public Kind getObjectKind() {
		return Proxiable.Kind.BEHAVIOR;
	}

	@Override
	public final boolean validate(ValidationContext context) {
		return getState().validate(context, SystemState.RUN_TIME);
	}

	@Override
	public final boolean validate(ValidationContext context,
			SystemState systemState) {
		return getState().validate(context, systemState);
	}

	@Override
	public <T extends Trait> T asTrait(String role) {
		return getState().asTrait(role);
	}

}
