package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping = JavaMapping.STATE_INTERFACE, isAbstract = true)
public interface InkTypeState extends InkObjectState {

	public class Data extends InkObjectState.Data implements InkTypeState {
	}
}
