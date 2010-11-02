package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorImpl;
import org.ink.core.vm.types.ReferenceTypeMarker;


/**
 * @author Lior Schachter
 */
public class ReferenceMirrorImpl<S extends PrimitiveAttributeMirrorState> extends ValuePropertyMirrorImpl<S> implements ReferenceMirror{
	
	@Override
	public boolean isValueContainsInkObject() {
		return true;
	}

	@Override
	public ReferenceTypeMarker getReferenceTypeMarker() {
		return null;
	}
}
