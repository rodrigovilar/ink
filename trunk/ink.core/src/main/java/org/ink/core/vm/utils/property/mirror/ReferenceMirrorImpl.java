package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorImpl;


/**
 * @author Lior Schachter
 */
public class ReferenceMirrorImpl<S extends PrimitiveAttributeMirrorState> extends ValuePropertyMirrorImpl<S> implements ReferenceMirror{
	
	@Override
	public boolean isValueContainsInkObject() {
		return true;
	}
}
