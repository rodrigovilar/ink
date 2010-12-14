package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.property.Attribute;
import org.ink.core.vm.types.PrimitiveType;


/**
 * @author Lior Schachter
 */
public interface PrimitiveAttribute extends Attribute {

	@Override
	public PrimitiveType getType();
}
