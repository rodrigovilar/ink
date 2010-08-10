package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping=JavaMapping.State_Interface)
public interface InkTypeState extends InkObjectState{
	
	public class Data extends InkObjectState.Data implements InkTypeState{
	}
}
