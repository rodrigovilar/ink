package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.ClassMirror;

/**
 * @author Lior Schachter
 */
public interface Trait extends InkObject {

	public void afterTargetSet();

	public <T extends InkObject> T getTargetBehavior();

	// public Set<InkClass> getTragetClasses();
	public boolean isAcceptable(ClassMirror cls);

}
