package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.ValuePropertyMirrorState;


/**
 * @author Lior Schachter
 */
public interface PrimitiveAttributeMirrorState extends ValuePropertyMirrorState{
	
	public class Data extends ValuePropertyMirrorState.Data implements PrimitiveAttributeMirrorState{
		
	}
	
}
