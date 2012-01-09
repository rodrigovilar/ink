package org.ink.core.vm.serialization;

import org.ink.core.vm.lang.InkObjectState;

/**
 * @author Lior Schachter
 */
public interface InkReaderState extends InkObjectState {

	public class Data extends InkObjectState.Data implements InkReaderState {
	}

}
