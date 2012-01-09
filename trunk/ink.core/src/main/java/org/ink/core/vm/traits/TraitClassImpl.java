package org.ink.core.vm.traits;

import java.util.List;

import org.ink.core.vm.lang.InkClassImpl;
import org.ink.core.vm.lang.Property;

/**
 * @author Lior Schachter
 */
public class TraitClassImpl<S extends TraitClassState> extends InkClassImpl<S> implements TraitClass {

	@Override
	public List<? extends Property> getInjectedTargetProperties() {
		return getState().getInjectedProperties();
	}

	@Override
	public TraitKind getKind() {
		return getState().getKind();
	}

	@Override
	public String getRole() {
		return getState().getRole();
	}
}
