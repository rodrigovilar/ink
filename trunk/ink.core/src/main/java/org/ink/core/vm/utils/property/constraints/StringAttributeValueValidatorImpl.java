package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.PropertyValueValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.utils.property.StringAttribute;

/**
 * @author Lior Schachter
 */
public class StringAttributeValueValidatorImpl<S extends StringAttributeValueValidatorState> extends ValidatorImpl<S> implements PropertyValueValidator{

	@Override
	public void validate(Property property, Object propertyValue, InkObjectState dataContainer, 
			ValidationContext context, SystemState systemState){
		if(propertyValue!=null && !context.containsError()){
			StringAttribute att = (StringAttribute)property;
			String str = (String)propertyValue;
			Integer limit;
			if((limit = att.getMinLength()) != null){
				if(str.length()<limit){
					context.addError(dataContainer, this, "min.length.violation", property.getName(), limit);
					return;
				}
			}
			if((limit = att.getMaxLength()) != null){
				if(str.length()>limit){
					context.addError(dataContainer, this, "max.length.violation", property.getName(), limit);
					return;
				}
			}
			String regExp = att.getRegularExpression();
			if(regExp!=null && !str.matches(regExp)){
				context.addError(dataContainer, this, "reg.exp.violation", property.getName(), regExp);
			}
		}
	}
	
}
