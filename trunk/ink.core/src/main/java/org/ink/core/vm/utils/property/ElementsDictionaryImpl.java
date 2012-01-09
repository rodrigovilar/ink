package org.ink.core.vm.utils.property;

import java.util.HashMap;
import java.util.Map;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.lang.InkType;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.proxy.Proxiable;

/**
 * @author Lior Schachter
 */
public class ElementsDictionaryImpl<S extends ElementsDictionaryState> extends DictionaryImpl<S> implements ElementsDictionary {

	private PropertyMirror keyMirror;
	private PropertyMirror valueMirror;

	public void init() {
		Property desc = getState().getItem();
		if (desc != null) {
			valueMirror = (PropertyMirror) desc.reflect();
			InkType type = valueMirror.getPropertyType();
			if (type.isObject()) {
				ClassMirror cm = type.reflect();
				PropertyMirror targetPm = cm.getClassPropertyMirror(getState().getKeyProperty());
				if (targetPm == null) {
					throw new CoreException("Could not find property " + getState().getKeyProperty() + ", in class " + cm.getId() + ".");
				}
				keyMirror = targetPm.cloneTargetState().reflect();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getDefaultValue() {
		Map result = null;
		Property desc = getState().getItem();
		if (desc != null) {
			Object mapValue = desc.getDefaultValue();
			if (mapValue != null && mapValue instanceof Proxiable) {
				Object mapKey = ((Proxiable) mapValue).reflect().getPropertyValue(getState().getKeyProperty());
				if (mapKey != null || mapValue != null) {
					result = new HashMap();
					result.put(mapKey, mapValue);
				}
			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getFinalValue() {
		Map result = null;
		Property desc = getState().getItem();
		if (desc != null) {
			Object mapValue = desc.getFinalValue();
			if (mapValue != null && mapValue instanceof Proxiable) {
				Object mapKey = ((Proxiable) mapValue).reflect().getPropertyValue(getState().getKeyProperty());
				if (mapKey != null || mapValue != null) {
					result = new HashMap();
					result.put(mapKey, mapValue);
				}
			}
		}
		return result;
	}

	@Override
	public PropertyMirror getValueMirror() {
		if (valueMirror == null) {
			init();
		}
		return valueMirror;
	}

	@Override
	public PropertyMirror getKeyMirror() {
		if (keyMirror == null) {
			init();
		}
		return keyMirror;
	}

	@Override
	public String getEntryName() {
		return getValueMirror().getName();
	}

}
