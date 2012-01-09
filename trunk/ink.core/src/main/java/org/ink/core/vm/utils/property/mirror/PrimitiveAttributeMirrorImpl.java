package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.DataTypeMarker;
import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorImpl;
import org.ink.core.vm.types.PrimitiveTypeMarker;
import org.ink.core.vm.utils.property.PrimitiveAttribute;

/**
 * @author Lior Schachter
 */
public class PrimitiveAttributeMirrorImpl<S extends PrimitiveAttributeMirrorState> extends ValuePropertyMirrorImpl<S> implements PrimitiveAttributeMirror {

	private PrimitiveTypeMarker primitiveTypeMarker;

	public void boot(byte index, String name, Class<?> typeClass, DataTypeMarker typeMarker, boolean hasStaticValue, boolean isComputed, PrimitiveTypeMarker primitiveTypeMarker) {
		super.boot(index, name, typeClass, typeMarker, hasStaticValue, isComputed);
		this.primitiveTypeMarker = primitiveTypeMarker;
	}

	@Override
	public void afterTargetSet() {
		super.afterTargetSet();
		primitiveTypeMarker = ((PrimitiveAttribute) getTargetBehavior()).getType().getPrimitiveMarker();
	}

	@Override
	public PrimitiveTypeMarker getPrimitiveTypeMarker() {
		return primitiveTypeMarker;
	}

}
