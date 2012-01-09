package org.ink.core.vm.utils.property.mirror;

import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;

/**
 * @author Lior Schachter
 */
public interface ListPropertyMirror extends CollectionPropertyMirror {

	public PropertyMirror getItemMirror();

}
