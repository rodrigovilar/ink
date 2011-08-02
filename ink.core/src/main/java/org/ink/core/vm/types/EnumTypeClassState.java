package org.ink.core.vm.types;

import org.ink.core.vm.constraints.ClassConstraintsState;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.mirror.ClassMirrorState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping=JavaMapping.Only_State,constraintsClass=ClassConstraintsState.class,
		mirrorClass=ClassMirrorState.class,finalValues={"Root"},
		finalValuesLocation={InkClassState.p_component_type})
public interface EnumTypeClassState extends InkClassState{

	public class Data extends InkClassState.Data implements EnumTypeClassState{

	}

}
