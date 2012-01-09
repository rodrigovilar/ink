package org.ink.core.vm.traits;

import org.ink.core.vm.mirror.ClassMirror;

/**
 * @author Lior Schachter
 */
public class ClassHierarchyLocatorImpl<S extends ClassHierarchyLocatorState> extends TargetLocatorImpl<S> {

	@Override
	public boolean accept(ClassMirror cls) {
		return cls.isSubClassOf(getState().getRootClass());
	}

}
