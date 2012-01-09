package org.ink.core.vm.factory;

import org.ink.core.vm.exceptions.ObjectLoadingException;
import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public class EmptyDslLoaderImpl<S extends EmptyDslLoaderState, D> extends DslLoaderImpl<S, D> {

	@Override
	public synchronized InkObjectState getObject(String id, Context context) throws ObjectLoadingException {
		return null;
	}

	@Override
	public void scan(DslFactory ownerFactory) {
	}

}
