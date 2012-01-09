package org.ink.core.vm.lang.operation.interceptors;

import java.lang.reflect.Method;
import java.util.Map;

import org.ink.core.vm.lang.InkObject;

/**
 * @author Lior Schachter
 */
public interface OperationInterceptor extends InkObject {

	public void beforeExceution(Method method, Object[] args, Object workOnObject, Map<?, ?> context);

	public void afterExceution(Method method, Object args[], Object workOnObject, Object response, Map<?, ?> context);

	public void afterException(Method method, Object args[], Object workOnObject, Throwable e, Map<?, ?> context);

}
