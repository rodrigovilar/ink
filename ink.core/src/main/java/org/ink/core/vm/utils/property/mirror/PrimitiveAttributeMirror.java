package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.ValuePropertyMirror;
import org.ink.core.vm.types.PrimitiveTypeMarker;



/**
 * @author Lior Schachter
 */
public interface PrimitiveAttributeMirror extends ValuePropertyMirror{
	public PrimitiveTypeMarker getPrimitiveTypeMarker();
}
