package org.ink.core.vm.lang.operation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.operation.interceptors.OperationInterceptor;

/**
 * @author Lior Schachter
 */
public class OperationImpl<S extends OperationState> extends InkObjectImpl<S> implements Operation {

	@Override
	public Object execute(InkObject target, Method method, Object[] args) throws Throwable {
		List<? extends OperationInterceptor> interceptors = getState().getInterceptors();
		if (interceptors != null) {
			Map<?, ?> context = new HashMap<Object, Object>();
			for (OperationInterceptor mi : interceptors) {
				mi.beforeExceution(method, args, target, context);
			}
			try {
				Object result = method.invoke(target, args);
				for (OperationInterceptor mi : interceptors) {
					mi.afterExceution(method, args, target, result, context);
				}
				return result;
			} catch (InvocationTargetException e) {
				Throwable cause = e.getTargetException();
				throw cause != null ? cause : e;
			} catch (Exception e) {
				for (OperationInterceptor mi : interceptors) {
					mi.afterException(method, args, target, e, context);
				}
				throw e;
			}
		} else {
			try {
				return method.invoke(target, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String getName() {
		return getState().getName();
	}

}
