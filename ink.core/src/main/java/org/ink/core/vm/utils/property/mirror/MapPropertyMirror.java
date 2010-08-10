package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;

/**
 * @author Lior Schachter
 */
public interface MapPropertyMirror extends CollectionPropertyMirror{
	
	public PropertyMirror getValueMirror();
	public PropertyMirror getKeyMirror();
	
}
