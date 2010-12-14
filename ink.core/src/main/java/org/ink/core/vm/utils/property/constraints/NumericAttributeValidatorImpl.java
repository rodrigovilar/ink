package org.ink.core.vm.utils.property.constraints;

import org.ink.core.vm.constraints.InstanceValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.property.NumericAttribute;
import org.ink.core.vm.utils.property.NumericAttributeState;

/**
 * @author Lior Schachter
 */
public class NumericAttributeValidatorImpl<S extends NumericAttributeValidatorState> extends ValidatorImpl<S> implements InstanceValidator{

	@Override
	public void validate(InkObjectState target, Mirror targetSuper,
			ValidationContext context, SystemState systemState) {
		NumericAttribute att = (NumericAttribute)target.getBehavior();
		Number minValue = att.getMinValue();
		Number maxValue = att.getMaxValue();
		Number defaultValue = att.getDefaultValue();
		if(minValue!=null && maxValue!=null && minValue.floatValue()>=maxValue.floatValue()){
			PropertyMirror prop1 = ((ClassMirror)target.getMeta().reflect()).getClassPropertyMirror(NumericAttributeState.p_max_value);
			PropertyMirror prop2 = ((ClassMirror)target.getMeta().reflect()).getClassPropertyMirror(NumericAttributeState.p_min_value);
			context.addError(target, this, "values.comparison.violation", prop1.getName(), maxValue, prop2.getName(), minValue);
		}
		if(defaultValue!=null){
			if(minValue !=null && defaultValue.doubleValue() <minValue.doubleValue()){
				PropertyMirror prop = ((ClassMirror)target.getMeta().reflect()).getClassPropertyMirror(NumericAttributeState.p_default_value);
				context.addError(target, this, "min.value.violation", prop.getName(), minValue);
				return;
			}else if(maxValue!=null && defaultValue.doubleValue()>maxValue.doubleValue()){
				PropertyMirror prop = ((ClassMirror)target.getMeta().reflect()).getClassPropertyMirror(NumericAttributeState.p_default_value);
				context.addError(target, this, "max.value.violation", prop.getName(), maxValue);
				return;
			}
		}
	}



}
