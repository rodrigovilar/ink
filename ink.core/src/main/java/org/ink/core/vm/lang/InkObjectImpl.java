package org.ink.core.vm.lang;

import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.factory.Context;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.traits.Trait;

/**
 * @author Lior Schachter
 */
public class InkObjectImpl<S extends InkObjectState> implements InkObject{
	private S state = null;
	private Context context = null;

	@SuppressWarnings("unchecked")
	protected void setState(InkObjectState state, Context context){
		this.state = (S) state;
		this.context = context;
	}

	protected S getState(){
		return state;
	}

	protected Context getContext(){
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
	public <M extends InkClass> M getMeta(){
		return state.getMeta();
	}

	@Override
	public void afterStateSet() {
	}

	@Override
	public <T extends InkObjectState> T cloneState(){
		return getState().cloneState();
	}

	@Override
	public boolean isProxied() {
		return false;
	}

	@Override
	public String toString() {
		return state.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
		    return true;
		}
		String id = state.getId();
		if(obj!=null && id!=null && obj instanceof InkObject){
			return id.equals(((InkObject)obj).reflect().getId());
		}
		return false;
	}

	@Override
	public Kind getObjectKind() {
		return Proxiable.Kind.Behavior;
	}

	@Override
	public boolean validate(ValidationContext context) {
		return getState().validate(context, SystemState.Run_Time);
	}

	@Override
	public boolean validate(ValidationContext context, SystemState systemState) {
		return getState().validate(context, systemState);
	}

}
