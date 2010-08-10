package org.ink.core.vm.factory;

import org.ink.core.vm.factory.internal.VMConfigImpl;

/**
 * @author Lior Schachter
 */
public abstract class VMConfig {
	
	private static VMConfig instance = VMConfigImpl.getInstance();

	public static VMConfig instance() {
		return instance;
	}

	public abstract InstantiationStrategy getInstantiationStrategy();
}