package org.ink.core.vm.types;

import org.ink.core.vm.lang.InkType;

/**
 * @author Lior Schachter
 */
public interface CollectionType extends InkType{
	public CollectionTypeMarker getCollectionTypeMarker();
}
