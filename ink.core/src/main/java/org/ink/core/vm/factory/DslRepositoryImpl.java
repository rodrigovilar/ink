package org.ink.core.vm.factory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public class DslRepositoryImpl<S extends DslRepositoryState> extends InkObjectImpl<S> implements DslRepository{

	private final Map<String, InkObjectState> store = new ConcurrentHashMap<String, InkObjectState>(500);

	@Override
	public InkObjectState getObject(String id) {
		return store.get(id);
	}

	@Override
	public void setObject(String id, InkObjectState result) {
		store.put(id, result);
	}

	@Override
	public Iterator<InkObjectState> iterator() {
		return store.values().iterator();
	}

	@Override
	public void clear() {
		store.clear();
	}

}
