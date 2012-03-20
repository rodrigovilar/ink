package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.InkClass;

/**
 * @author Lior Schachter
 */
public interface ModelPathAttribute extends PrimitiveAttribute {
	@Override
	public String getDefaultValue();
	@Override
	public String getFinalValue();
	public InkClass getPathRoot();
}
