package org.ink.core.vm.utils.property.mirror;

import java.util.Map;

import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.utils.property.Dictionary;

/**
 * @author Lior Schachter
 */
public interface MapPropertyMirror extends CollectionPropertyMirror {

	public Map<?, ?> getNewInstance();

	public PropertyMirror getValueMirror();

	public PropertyMirror getKeyMirror();

	public Dictionary getSpecifictation();

}
