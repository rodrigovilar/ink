package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.property.ValueProperty;

/**
 * @author Lior Schachter
 */
public interface Reference extends ValueProperty {
	
	public ReferenceKind getKind();
	
	@Override
	public InkClass getType();
	
	@Override
	public InkObject getDefaultValue();
	
}
