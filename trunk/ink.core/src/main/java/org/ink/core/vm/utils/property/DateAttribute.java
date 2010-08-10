package org.ink.core.vm.utils.property;

import java.util.Date;



/**
 * @author Lior Schachter
 */
public interface DateAttribute extends PrimitiveAttribute {
	@Override
	public Date getDefaultValue();
}
