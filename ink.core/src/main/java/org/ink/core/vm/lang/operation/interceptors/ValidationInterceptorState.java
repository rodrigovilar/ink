package org.ink.core.vm.lang.operation.interceptors;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping = JavaMapping.State_Behavior)
public interface ValidationInterceptorState extends OperationInterceptorState {
	public class Data extends InkObjectState.Data implements ValidationInterceptorState {
	}

}
