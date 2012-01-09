package org.ink.core.vm.lang.operation.interceptors;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(isAbstract = true)
public interface OperationInterceptorState extends InkObjectState {
	public class Data extends InkObjectState.Data implements OperationInterceptorState {
	}

}
