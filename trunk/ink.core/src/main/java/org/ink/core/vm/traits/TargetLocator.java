package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.ClassMirror;

/**
 * @author Lior Schachter
 */
public interface TargetLocator extends InkObject {

	public boolean accept(ClassMirror cls);

}
