package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.PropertyValueValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.utils.property.ModelPathAttribute;

/**
 * @author Lior Schachter
 */
public class ModelPathAttributeValueValidatorImpl<S extends ModelPathAttributeValueValidatorState> extends ValidatorImpl<S> implements PropertyValueValidator {

	@Override
	public void validate(Property property, Object propertyValue, InkObjectState dataContainer, ValidationContext context, SystemState systemState) {
		if (propertyValue != null && !context.containsError()) {
			ModelPathAttribute att = (ModelPathAttribute) property;
			String path = (String) propertyValue;
			InkClass root = att.getPathRoot();
			ClassMirror cm;
			if(root!=null){
				cm = root.reflect();
			}else{
				cm = dataContainer.reflect().getRootOwner().getClassMirror();
			}
			String msg = cm.validatePath(path);
			if(msg!=null){
				context.addError(dataContainer, this, "invalid.model.path",path, msg);
			}
		}
	}

}
