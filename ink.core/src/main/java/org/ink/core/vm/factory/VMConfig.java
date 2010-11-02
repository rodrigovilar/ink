package org.ink.core.vm.factory;

import org.ink.core.vm.factory.internal.FullInstantiationStrategy;

/**
 * @author Lior Schachter
 */
public class VMConfig {
	
	private static final VMConfig INSTANCE = new VMConfig();
	private static InstantiationStrategy initStrategy = new FullInstantiationStrategy();
	
	private VMConfig() {
	}
	
	public static VMConfig instance() {
		return INSTANCE;
	}
	
	public static void setInstantiationStrategy(InstantiationStrategy strategy){
		initStrategy = strategy;
	}

	public InstantiationStrategy getInstantiationStrategy() {
		return initStrategy;
	}
}