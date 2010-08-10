package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkClassImpl;


/**
 * @author Lior Schachter
*/
public class ValidationContextClassImpl<S extends ValidationContextClassState> extends InkClassImpl<S> implements ValidationContextClass{

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ValidationMessage> T instantiateErrorMessage() {
		return (T)getState().getErrorMessageClass().newInstance().getBehavior();
	}

		
}
