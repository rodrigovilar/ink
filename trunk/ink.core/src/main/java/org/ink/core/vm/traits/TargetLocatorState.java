package org.ink.core.vm.traits;

import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface TargetLocatorState extends InkObjectState {

	public class Data extends InkObjectState.Data implements TargetLocatorState {
	}
}
