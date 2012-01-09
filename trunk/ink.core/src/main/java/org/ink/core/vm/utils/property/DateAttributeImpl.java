package org.ink.core.vm.utils.property;

import java.util.Date;

/**
 * @author Lior Schachter
 */
public class DateAttributeImpl<S extends DateAttributeState> extends PrimitiveAttributeImpl<S> implements DateAttribute {

	@Override
	public Date getDefaultValue() {
		return getState().getDefaultValue();
	}

	@Override
	public Date getFinalValue() {
		return getState().getFinalValue();
	}

}
