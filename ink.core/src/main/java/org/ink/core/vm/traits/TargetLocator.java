package org.ink.core.vm.traits;

import java.util.Set;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;

/**
 * @author Lior Schachter
 */
public interface TargetLocator extends InkObject {

	public Set<InkClass> match();
	public boolean isAcceptable(InkClass cls);

}
