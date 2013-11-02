package org.ink.core.vm.factory.internal;

import org.ink.core.vm.factory.DslLoaderState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping = JavaMapping.STATE_BEHAVIOR)
public interface CoreLoaderState extends DslLoaderState {
	public class Data extends DslLoaderState.Data implements CoreLoaderState {
	}
}
