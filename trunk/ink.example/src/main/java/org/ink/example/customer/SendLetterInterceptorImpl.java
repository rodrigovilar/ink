package org.ink.example.customer;

import java.lang.reflect.Method;
import java.util.Map;

import org.ink.core.vm.lang.operation.interceptors.OperationInterceptorImpl;

public class SendLetterInterceptorImpl<S extends SendLetterInterceptorState> extends OperationInterceptorImpl<S> {

	@Override
	public void beforeExceution(Method method, Object[] args,
			Object workOnObject, Map<?, ?> context) {
		if(((Customer)workOnObject).getFirstName().equals(getState().getRestrictedFirstName())){
			throw new RuntimeException();
		}
	}

	@Override
	public void afterExceution(Method method, Object[] args,
			Object workOnObject, Object response, Map<?, ?> context) {
	}

	@Override
	public void afterException(Method method, Object[] args,
			Object workOnObject, Throwable e, Map<?, ?> context) {

	}

}
