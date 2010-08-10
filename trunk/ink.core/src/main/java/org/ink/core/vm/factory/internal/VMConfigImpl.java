package org.ink.core.vm.factory.internal;

import org.ink.core.vm.factory.InstantiationStrategy;
import org.ink.core.vm.factory.VMConfig;

/**
 * @author Lior Schachter
 */
public class VMConfigImpl extends VMConfig {
	
	private static final VMConfig INSTANCE = new VMConfigImpl();
	
	private VMConfigImpl() {
	}
	
	public static VMConfig getInstance() {
		return INSTANCE;
	}

	@Override
	public InstantiationStrategy getInstantiationStrategy() {
		return FullInstantiationStrategy.getInstance();
	}

}