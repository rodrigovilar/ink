package org.ink.core.vm.utils.property;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ink.core.vm.lang.InkObjectImpl;

/**
 * @author Lior Schachter
 */
public abstract class DictionaryImpl<S extends DictionaryState> extends InkObjectImpl<S> implements Dictionary {

	@Override
	public Map<?, ?> getNewInstance() {
		return getState().getOrdered() ? new LinkedHashMap<Object, Object>() : new HashMap<Object, Object>();
	}

}