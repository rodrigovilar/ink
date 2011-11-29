package org.ink.core.vm.lang;

import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass=StructClassState.class, javaMapping=JavaMapping.Only_State)
public interface Struct extends InkObjectState{

	public class Data extends InkObjectState.Data implements Struct{

		@Override
		public boolean canHaveBehavior() {
			return false;
		}

		@Override
		public final <T extends InkObject> T getBehavior() {
			throw new UnsupportedOperationException("Could not create behavior for object '"+getId()+"':a struct don't have a behavior.");
		}

	}
}
