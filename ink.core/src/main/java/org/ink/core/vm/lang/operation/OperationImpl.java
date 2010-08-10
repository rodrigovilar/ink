package org.ink.core.vm.lang.operation;

import java.lang.reflect.Method;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.TypedObjectImpl;

/**
 * @author Lior Schachter
 */
public class OperationImpl<S extends OperationState> extends TypedObjectImpl<S> implements Operation{

	@Override
	public Object execute(InkObject target, Method method, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}


}
