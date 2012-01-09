package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(isAbstract = true, mirrorClass = PrimitiveAttributeMirrorState.class, constraintsClass = PropertyConstraintsState.class)
public interface NumericAttributeState extends PrimitiveAttributeState {

	@CoreField(mandatory = false)
	public static final byte p_default_value = 7;
	@CoreField(mandatory = false)
	public static final byte p_final_value = 8;
	@CoreField(mandatory = false)
	public static final byte p_min_value = 9;
	@CoreField(mandatory = false)
	public static final byte p_max_value = 10;

	public class Data extends PrimitiveAttributeState.Data implements NumericAttributeState {
	}
}
