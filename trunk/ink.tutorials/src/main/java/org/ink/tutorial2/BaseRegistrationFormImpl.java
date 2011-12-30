package org.ink.tutorial2;

import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;

public class BaseRegistrationFormImpl<S extends BaseRegistrationFormState>
		extends InkObjectImpl<S> implements BaseRegistrationForm {

	@Override
	public String serialize() {
		String result = "";

		Mirror mirror = this.reflect();
		PropertyMirror[] propertiesMirrors = mirror.getPropertiesMirrors();
		for (PropertyMirror propertyMirror : propertiesMirrors) {
			String name = propertyMirror.getName();
			Object value = mirror.getPropertyValue(propertyMirror.getIndex());
			result = result + name + "='"+value+"',";
		}
		result = result.substring(0,result.length()-1);

		return result;
	}

}