package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.property.ValuePropertyImpl;
import org.ink.core.vm.proxy.Proxiable;


/**
 * @author Lior Schachter
 */
public class ReferenceImpl<S extends ReferenceState> extends ValuePropertyImpl<S> implements Reference {

	@Override
	public ReferenceKind getKind() {
		return getState().getKind();
	}

	@Override
	public InkClass getType() {
		return getState().getType();
	}

	@Override
	public Proxiable getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public Proxiable getFinalValue() {
		return getState().getFinalValue();
	}

}
