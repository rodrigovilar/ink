package org.ink.core.vm.utils.property.constraints;

import java.util.Collection;
import java.util.Iterator;

import org.ink.core.vm.constraints.InstanceValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.types.EnumType;
import org.ink.core.vm.utils.property.EnumAttributeState;

/**
 * @author Lior Schachter
 */
public class EnumAttributeValidatorImpl<S extends EnumAttributeValidatorState> extends ValidatorImpl<S> implements InstanceValidator {

	@Override
	public void validate(InkObjectState target, Mirror targetSuper, ValidationContext context, SystemState systemState) {
		EnumAttributeState att = (EnumAttributeState) target;
		String defaultValue = att.getDefaultValue();
		if (defaultValue != null) {
			EnumType eType = att.getType();
			Collection<String> vals = eType.getValues();
			if (!vals.contains(defaultValue)) {
				String values = "";
				Iterator<String> iter = vals.iterator();
				for (int i = 0; i < vals.size(); i++) {
					values += iter.next();
					if (i < vals.size() - 1) {
						values += ",";
					}
				}
				context.addError(target, this, "enum.illegal.value", defaultValue, eType.reflect().getId(), values);
			}
			return;
		}
	}

}
