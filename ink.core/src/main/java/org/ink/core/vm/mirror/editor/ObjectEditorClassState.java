package org.ink.core.vm.mirror.editor;

import org.ink.core.vm.constraints.ClassConstraintsState;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.mirror.ClassMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(constraintsClass = ClassConstraintsState.class, mirrorClass = ClassMirrorState.class, finalValuesLocation = InkClassState.p_can_cache_behavior_instance, finalValues = "false", javaMapping = JavaMapping.ONLY_STATE)
public interface ObjectEditorClassState extends InkClassState {

	public class Data extends InkClassState.Data implements ObjectEditorClassState {

	}

}
