package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.mirror.ClassMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(constraintsClass = ClassConstraintsState.class, mirrorClass = ClassMirrorState.class, javaMapping = JavaMapping.ONLY_STATE, finalValuesLocation = { InkClassState.p_can_cache_behavior_instance }, finalValues = { "true" })
public interface ValidationMessageClassState extends InkClassState {

	public class Data extends InkClassState.Data implements ValidationMessageClassState {
	}

}
