package org.ink.core.vm.traits;

import java.util.List;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.Property;

/**
 * @author Lior Schachter
 */
public interface TraitClass extends InkClass {
	
	public String getRole();
	public TraitKind getKind();
	public List<? extends Property> getInjectedTargetProperties();

}
