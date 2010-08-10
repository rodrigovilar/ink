package org.ink.core.vm.lang.property.mirror;

import org.ink.core.vm.types.CollectionTypeMarker;



/**
 * @author Lior Schachter
 */
public interface CollectionPropertyMirror extends PropertyMirror{
	
	public CollectionTypeMarker getCollectionTypeMarker();
	
}
