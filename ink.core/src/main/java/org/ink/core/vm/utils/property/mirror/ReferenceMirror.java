package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.ValuePropertyMirror;
import org.ink.core.vm.types.ReferenceTypeMarker;

/**
 * @author Lior Schachter
 */
public interface ReferenceMirror extends ValuePropertyMirror {

	public ReferenceTypeMarker getReferenceTypeMarker();
}
