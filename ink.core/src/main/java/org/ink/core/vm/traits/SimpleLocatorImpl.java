package org.ink.core.vm.traits;

import java.util.HashSet;
import java.util.Set;

import org.ink.core.vm.lang.InkClass;


/**
 * @author Lior Schachter
 */
public class SimpleLocatorImpl<S extends SimpleLocatorState> extends TargetLocatorImpl<S>{

	@Override
	public Set<InkClass> match() {
		Set<InkClass> result = new HashSet<InkClass>();
		result.add(getState().getRootClass());
		//TODO - should add all sub-classses (when profile DB is ready)
		return result;
	}
	
	
}
