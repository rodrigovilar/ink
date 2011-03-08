package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorImpl;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.StructClassMirror;
import org.ink.core.vm.types.ReferenceTypeMarker;
import org.ink.core.vm.utils.property.ReferenceState;


/**
 * @author Lior Schachter
 */
public class ReferenceMirrorImpl<S extends ReferenceMirrorState> extends ValuePropertyMirrorImpl<S> implements ReferenceMirror{

	@Override
	public boolean isValueContainsInkObject() {
		return true;
	}

	@Override
	public ReferenceTypeMarker getReferenceTypeMarker() {
		ReferenceState refState = getTargetState();
		ClassMirror type = refState.getType().reflect();
		//todo - remove instance of
		if(type instanceof StructClassMirror){
			return ReferenceTypeMarker.Struct;
		}
		return ReferenceTypeMarker.Object;
	}

}
