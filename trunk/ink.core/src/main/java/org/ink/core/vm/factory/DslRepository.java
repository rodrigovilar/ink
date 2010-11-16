package org.ink.core.vm.factory;

import java.util.Iterator;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface DslRepository extends InkObject, Iterable<InkObjectState> {

	@Override
	public Iterator<InkObjectState> iterator();

	InkObjectState getObject(String id);

	void setObject(String id, InkObjectState result);

}
