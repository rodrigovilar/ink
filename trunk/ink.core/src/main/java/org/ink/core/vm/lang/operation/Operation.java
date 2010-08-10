package org.ink.core.vm.lang.operation;

import java.lang.reflect.Method;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.TypedObject;

/**
 * @author Lior Schachter
 */
public interface Operation extends TypedObject{

	Object execute(InkObject target, Method method, Object[] args);

}
