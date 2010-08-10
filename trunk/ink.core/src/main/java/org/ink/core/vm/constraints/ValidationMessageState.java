package org.ink.core.vm.constraints;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass=ValidationMessageClassState.class)
public interface ValidationMessageState extends InkObjectState{
	public class Data extends InkObjectState.Data implements ValidationMessageState{
	}
}
