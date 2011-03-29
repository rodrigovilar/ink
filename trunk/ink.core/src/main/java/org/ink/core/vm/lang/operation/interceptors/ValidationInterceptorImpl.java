package org.ink.core.vm.lang.operation.interceptors;

import java.lang.reflect.Method;
import java.util.Map;

import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.proxy.Proxiable;


/**
 * @author Lior Schachter
 */
public class ValidationInterceptorImpl<S extends ValidationInterceptorState> extends OperationInterceptorImpl<S> implements OperationInterceptor{


	@Override
	public void beforeExceution(Method method, Object[] args, Object workOnObject, Map<?, ?> context) {
		if(args!=null){
			ValidationContext vc = getContext().newInstance(CoreNotations.Ids.VALIDATION_CONTEXT).getBehavior();
			boolean result=true;
			for(Object o : args){
				if(o instanceof Proxiable){
					Proxiable inkObject = (Proxiable)o;
					switch(inkObject.getObjectKind()){
						case Behavior:
							result = ((InkObject)inkObject).validate(vc);
							break;
						default:
							result = ((InkObjectState)inkObject).validate(vc);
							break;
					}
					if(!result){
						System.out.println("Invalid method arg was found. Method execution will not continue.");
						vc.logMessages();
						throw new org.ink.core.vm.exceptions.ValidationException("Validation failed on object with id " + inkObject.reflect().getId());
					}
				}
			}

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
