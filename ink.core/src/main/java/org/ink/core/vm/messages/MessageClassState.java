package org.ink.core.vm.messages;

import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.mirror.ClassMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=ClassMirrorState.class, javaMapping=JavaMapping.Only_State,
		finalValuesLocation={InkClassState.p_component_type},finalValues={"Root"})
public interface MessageClassState extends InkClassState{
		
	public class Data extends InkClassState.Data implements MessageClassState{
	}

}
