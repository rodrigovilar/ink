package org.ink.core.vm.factory;

public interface InstanceFactory {

	public Object newInstance(String namespace, Class<?> clazz);
}
