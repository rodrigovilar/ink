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

	public InkObjectState getObject(String id);

	public void setObject(String id, InkObjectState result);

	public void clear();

}
