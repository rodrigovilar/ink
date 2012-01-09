package org.ink.core.vm.mirror;

import org.ink.core.vm.lang.internal.MirrorAPI;
import org.ink.core.vm.proxy.Proxiability;

/**
 * @author Lior Schachter
 */
public class StructClassMirrorImpl<S extends StructClassMirrorState> extends ClassMirrorImpl<S> implements StructClassMirror {

	private Class<?>[] stateProxyInterfaces;

	@Override
	public void afterTargetSet() {
		super.afterTargetSet();
		stateProxyInterfaces = new Class[] { getStateInterface(), MirrorAPI.class, Proxiability.class };
	}

	@Override
	public Class<?>[] getStateProxyInterfaces() {
		return stateProxyInterfaces;
	}

	@Override
	public boolean isStruct() {
		return true;
	}

}
