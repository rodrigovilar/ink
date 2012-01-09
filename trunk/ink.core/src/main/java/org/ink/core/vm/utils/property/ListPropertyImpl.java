package org.ink.core.vm.utils.property;

import java.util.ArrayList;
import java.util.List;

import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.CollectionPropertyImpl;

/**
 * @author Lior Schachter
 */
public class ListPropertyImpl<S extends ListPropertyState> extends CollectionPropertyImpl<S> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getDefaultValue() {
		List result = null;
		Property itemDesc = getState().getListItem();
		if (itemDesc != null) {
			Object value = itemDesc.getDefaultValue();
			if (value != null) {
				result = new ArrayList();
				result.add(value);
			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getFinalValue() {
		List result = null;
		Property itemDesc = getState().getListItem();
		if (itemDesc != null) {
			Object value = itemDesc.getFinalValue();
			if (value != null) {
				result = new ArrayList();
				result.add(value);
			}
		}
		return result;
	}

}
