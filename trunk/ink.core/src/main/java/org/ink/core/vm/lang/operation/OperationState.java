package org.ink.core.vm.lang.operation;

import org.ink.core.vm.lang.TypedObjectState;

/**
 * @author Lior Schachter
 */
public interface OperationState extends TypedObjectState{
	public class Data extends TypedObjectState.Data implements OperationState{
	}

}
