package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.property.AttributeState;
import org.ink.core.vm.types.PrimitiveType;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=PrimitiveAttributeMirrorState.class,
		constraintsClass=PropertyConstraintsState.class, isAbstract=true)
public interface PrimitiveAttributeState extends AttributeState{

	@Override
	public PrimitiveType getType();

	public class Data extends AttributeState.Data implements PrimitiveAttributeState{

		@Override
		public PrimitiveType getType() {
			return (PrimitiveType)getValue(p_type);
		}

	}
}
