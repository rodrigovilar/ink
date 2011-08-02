package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.mirror.TraitMirrorState;
import org.ink.core.vm.traits.TraitClassState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping=JavaMapping.State_Behavior, metaclass=TraitClassState.class, mirrorClass=TraitMirrorState.class)
public interface ClassConstraintsState extends ConstraintsState{

	public class Data extends ConstraintsState.Data implements ClassConstraintsState{

	}

}
