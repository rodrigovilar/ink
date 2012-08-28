package org.ink.core.vm.factory.internal;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.InstanceFactory;

public class InstanceFactoryImpl implements InstanceFactory {

	@Override
	public Object newInstance(String namespace, Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new CoreException("Could not instantiate class : " + clazz.getName(), e);
		} catch (IllegalAccessException e) {
			throw new CoreException("Could not instantiate class : " + clazz.getName(), e);
		}
	}
}