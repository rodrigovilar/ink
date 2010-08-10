package org.ink.core.vm.lang;

import org.ink.core.vm.factory.DslFactoryState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass=DslFactoryState.class)
public interface ObjectFactoryState extends InkObjectState{
	
	public class Data extends InkObjectState.Data implements ObjectFactoryState{
		
	}

}
