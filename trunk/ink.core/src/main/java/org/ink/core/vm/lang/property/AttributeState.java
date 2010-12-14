package org.ink.core.vm.lang.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorState;



/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=ValuePropertyMirrorState.class,
		constraintsClass=PropertyConstraintsState.class, isAbstract=true)
public interface AttributeState extends ValuePropertyState{
	public class Data extends ValuePropertyState.Data implements AttributeState{

	}

}
