package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.property.AttributeImpl;
import org.ink.core.vm.types.PrimitiveType;


/**
 * @author Lior Schachter
 */
public abstract class PrimitiveAttributeImpl<S extends PrimitiveAttributeState> extends AttributeImpl<S> implements PrimitiveAttribute {

	@Override
	public PrimitiveType getType() {
		return getState().getType();
	}
}
