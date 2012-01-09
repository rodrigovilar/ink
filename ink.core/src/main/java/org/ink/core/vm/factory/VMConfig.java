package org.ink.core.vm.factory;

import org.ink.core.vm.factory.resources.DefaultResourceResolver;
import org.ink.core.vm.factory.resources.ResourceResolver;

/**
 * @author Lior Schachter
 */
public class VMConfig {

	private static final VMConfig INSTANCE = new VMConfig();
	private static ResourceResolver initStrategy = new DefaultResourceResolver();

	private VMConfig() {
	}

	public static VMConfig instance() {
		return INSTANCE;
	}

	public static void setInstantiationStrategy(ResourceResolver strategy) {
		initStrategy = strategy;
	}

	public ResourceResolver getResourceResolver() {
		return initStrategy;
	}
}