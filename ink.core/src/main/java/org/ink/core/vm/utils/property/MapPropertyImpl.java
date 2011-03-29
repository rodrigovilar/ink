package org.ink.core.vm.utils.property;

import java.util.HashMap;
import java.util.Map;

import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.CollectionPropertyImpl;



/**
 * @author Lior Schachter
 */
public class MapPropertyImpl<S extends MapPropertyState> extends CollectionPropertyImpl<S>{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getDefaultValue() {
		Map result = null;
		Object mapKey = null;
		Object mapValue = null;
		Property desc = getState().getMapKey();
		if(desc!=null){
			mapKey = desc.getDefaultValue();
		}
		desc = getState().getMapValue();
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
		Property desc = getState().getMapKey();
		if(desc!=null){
			mapKey = desc.getDefaultValue();
		}
		desc = getState().getMapValue();
		if(desc!=null){
			mapValue = desc.getDefaultValue();
		}
		if(mapKey!=null || mapValue!=null){
			result = new HashMap();
			result.put(mapKey, mapValue);
		}
		return result;
	}

}
