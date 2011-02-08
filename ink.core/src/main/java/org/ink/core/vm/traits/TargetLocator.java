package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;

/**
 * @author Lior Schachter
 */
public interface TargetLocator extends InkObject {

	public boolean accept(InkClass cls);

}
