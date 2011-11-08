package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.property.ValueProperty;
import org.ink.core.vm.proxy.Proxiable;

/**
 * @author Lior Schachter
 */
public interface Reference extends ValueProperty {

	public ReferenceKind getKind();

	@Override
	public InkClass getType();

	@Override
	public Proxiable getDefaultValue();

	@Override
	public Proxiable getFinalValue();

}
