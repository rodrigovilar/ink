package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkObjectImpl;

/**
 * @author Lior Schachter
 */
public abstract class ValidatorImpl<S extends ValidatorState> extends InkObjectImpl<S> implements Validator {

	@Override
	public boolean shouldValidate(SystemState systemState) {
		return ((ValidatorClass) getMeta()).shouldValidate(systemState);
	}

}
