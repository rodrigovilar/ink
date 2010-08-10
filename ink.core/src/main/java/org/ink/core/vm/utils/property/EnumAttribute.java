package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.property.Attribute;
import org.ink.core.vm.types.EnumType;


/**
 * @author Lior Schachter
 */
public interface EnumAttribute extends Attribute {
	
	@Override
	public EnumType getType();
}
