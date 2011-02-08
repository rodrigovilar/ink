package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.mirror.ClassMirror;


/**
 * @author Lior Schachter
 */
public class ClassHierarchyLocatorImpl<S extends ClassHierarchyLocatorState> extends TargetLocatorImpl<S>{


	@Override
	public boolean accept(InkClass cls) {
		ClassMirror cm = cls.reflect();
		return cm.isSubClassOf(getState().getRootClass());
	}


}
