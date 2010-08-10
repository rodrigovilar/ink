package org.ink.core.vm.utils.property;

import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.property.Attribute;
import org.ink.core.vm.types.PrimitiveType;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=PrimitiveAttributeMirrorState.class)
public interface PrimitiveAttribute extends Attribute {
	
	@Override
	public PrimitiveType getType();
}
