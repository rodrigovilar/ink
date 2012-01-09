package org.ink.core.vm.utils.property;

import java.util.Map;

import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;

/**
 * @author Lior Schachter
 */
public interface Dictionary extends InkObject {

	public Map<?, ?> getNewInstance();

	public PropertyMirror getKeyMirror();

	public PropertyMirror getValueMirror();

	public Object getDefaultValue();

	public Object getFinalValue();

	public String getEntryName();

}
