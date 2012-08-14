package org.ink.core.vm.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
	@Override
	public List<File> getInkFiles() {
		return new ArrayList<File>();
	}

}
