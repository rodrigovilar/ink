package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.property.CollectionPropertyImpl;



/**
 * @author Lior Schachter
 */
public class MapPropertyImpl<S extends MapPropertyState> extends CollectionPropertyImpl<S>{

	@Override
	public Object getDefaultValue() {
		Dictionary dic = getState().getSpecifications();
		if(dic!=null){
			return dic.getDefaultValue();
		}
		return null;
	}

	@Override
	public Object getFinalValue() {
		Dictionary dic = getState().getSpecifications();
		if(dic!=null){
			return dic.getFinalValue();
		}
		return null;
	}

}
