package org.ink.core.vm.factory.internal;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.factory.InstanceFactory;

public class InstanceFactoryImpl implements InstanceFactory {

	@Override
	public Object newInstance(String namespace, String className) {
		try {
			return Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			throw new CoreException("Could not instantiate class : " + className, e);
		} catch (IllegalAccessException e) {
			throw new CoreException("Could not instantiate class : " + className, e);
		} catch (ClassNotFoundException e) {
			throw new CoreException("Could not instantiate class : " + className, e);
		}
	}
}