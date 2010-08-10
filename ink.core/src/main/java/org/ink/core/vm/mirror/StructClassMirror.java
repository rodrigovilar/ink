package org.ink.core.vm.mirror;

/**
 * @author Lior Schachter
 */
public interface StructClassMirror extends ClassMirror{
	
	public Class<?>[] getStateProxyInterfaces();
	
}
