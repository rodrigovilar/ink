package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.mirror.TraitMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass=TraitClassState.class, mirrorClass=TraitMirrorState.class)
public interface TraitState extends InkObjectState{
	
	public static final byte p_target_locator = 0;
	
	public TargetLocator getTragetLocator();
	public void setTargetLocator(TargetLocatorState value);
	
	public class Data extends InkObjectState.Data implements TraitState{
		
		@Override
		public TargetLocator getTragetLocator() {
			return (TargetLocator) getValue(p_target_locator);
		}

		@Override
		public void setTargetLocator(TargetLocatorState value) {
			setValue(p_target_locator, value);
		}

	}
}
