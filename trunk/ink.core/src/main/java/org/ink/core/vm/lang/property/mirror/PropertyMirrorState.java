package org.ink.core.vm.lang.property.mirror;

import org.ink.core.vm.mirror.MirrorState;


/**
 * @author Lior Schachter
 */
public interface PropertyMirrorState extends MirrorState{
	
	public class Data extends MirrorState.Data implements PropertyMirrorState{
		
	}

}
