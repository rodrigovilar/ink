package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass = StructClassState.class, javaMapping = JavaMapping.ONLY_STATE)
public interface Struct extends InkObjectState {

	public class Data extends InkObjectState.Data implements Struct {

		@Override
		public boolean canHaveBehavior() {
			return false;
		}

	}
}
