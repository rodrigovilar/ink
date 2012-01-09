package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirrorState;

/**
 * @author Lior Schachter
 */
public interface ListPropertyMirrorState extends CollectionPropertyMirrorState {

	public class Data extends CollectionPropertyMirrorState.Data implements ListPropertyMirrorState {

	}

}
