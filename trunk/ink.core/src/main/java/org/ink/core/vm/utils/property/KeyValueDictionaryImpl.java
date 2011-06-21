package org.ink.core.vm.utils.property;

import java.util.HashMap;
import java.util.Map;

import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;


/**
 * @author Lior Schachter
 */
public class KeyValueDictionaryImpl<S extends KeyValueDictionaryState> extends DictionaryImpl<S> implements KeyValueDictionary {

	private PropertyMirror valueMirror;
	private PropertyMirror keyMirror;

	@Override
	public void afterStateSet() {
		super.afterStateSet();
		Property desc = getState().getValue();
		if(desc!=null){
			valueMirror = (PropertyMirror)desc.reflect();
		}
		desc = getState().getKey();
		if(desc!=null){
			keyMirror = (PropertyMirror)desc.reflect();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getDefaultValue() {
		Map result = null;
		Object mapKey = null;
		Object mapValue = null;
		Property desc = getState().getKey();
		if(desc!=null){
			mapKey = desc.getDefaultValue();
		}
		desc = getState().getValue();
		if(desc!=null){
			mapValue = desc.getDefaultValue();
		}
		if(mapKey!=null || mapValue!=null){
			result = new HashMap();
			result.put(mapKey, mapValue);
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getFinalValue() {
		Map result = null;
		Object mapKey = null;
		Object mapValue = null;
		Property desc = getState().getKey();
		if(desc!=null){
			mapKey = desc.getFinalValue();
		}
		desc = getState().getValue();
		if(desc!=null){
			mapValue = desc.getFinalValue();
		}
		if(mapKey!=null || mapValue!=null){
			result = new HashMap();
			result.put(mapKey, mapValue);
		}
		return result;
	}

	@Override
	public PropertyMirror getKeyMirror() {
		return keyMirror;
	}

	@Override
	public PropertyMirror getValueMirror() {
		return valueMirror;
	}

	@Override
	public String getEntryName() {
		return getState().getEntryName();
	}


}
