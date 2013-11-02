package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreEnumField;

/**
 * @author Lior Schachter
 */
public enum ComponentType {
	ROOT("Root"), PURE_COMPONENT("Pure_Component"), @CoreEnumField(isDefault = true)
	ROOT_OR_PURE_COMPONENT("Root_or_Pure_Component");
	
	public final String key;

	private ComponentType(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return key;
	}

}
