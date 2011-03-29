package org.ink.core.vm.lang.operation;

import java.lang.reflect.Method;

import org.ink.core.vm.lang.InkObject;

/**
 * @author Lior Schachter
 */
public interface Operation extends InkObject{

	public Object execute(InkObject target, Method method, Object[] args) throws Throwable;
	public String getName();

}
