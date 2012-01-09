package org.ink.core.vm.utils.property.constraints;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.ink.core.vm.constraints.InstanceValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.ValidatorImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.utils.property.StringAttributeState;

/**
 * @author Lior Schachter
 */
public class StringAttributeValidatorImpl<S extends StringAttributeValidatorState> extends ValidatorImpl<S> implements InstanceValidator {

	@Override
	public void validate(InkObjectState target, Mirror targetSuper, ValidationContext context, SystemState systemState) {
		StringAttributeState att = (StringAttributeState) target;
		Integer minLength = att.getMinLength();
		Integer maxLength = att.getMaxLength();
		String regExp = att.getRegExp();
		String defaultValue = att.getDefaultValue();
		if (minLength != null) {
			if (minLength < 0) {
				PropertyMirror prop = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_min_length);
				context.addError(target, this, "min.value.violation", prop.getName(), "0");
				return;
			} else if (defaultValue != null && defaultValue.length() < minLength) {
				PropertyMirror prop = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_min_length);
				context.addError(target, this, "min.length.violation", prop.getName(), minLength);
				return;
			}
		}
		if (maxLength != null) {
			if (maxLength < 1) {
				PropertyMirror prop = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_max_length);
				context.addError(target, this, "min.value.violation", prop.getName(), "1");
				return;
			} else if (minLength != null && maxLength < minLength) {
				PropertyMirror prop1 = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_max_length);
				PropertyMirror prop2 = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_min_length);
				context.addError(target, this, "values.comparison.violation", prop1.getName(), maxLength, prop2.getName(), minLength);
				return;
			} else if (defaultValue != null && defaultValue.length() > maxLength) {
				PropertyMirror prop = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_max_length);
				context.addError(target, this, "max.length.violation", prop.getName(), minLength);
				return;
			}
		}
		if (regExp != null) {
			if (defaultValue == null) {
				try {
					Pattern.compile(regExp);
				} catch (PatternSyntaxException e) {
					PropertyMirror prop = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_reg_exp);
					context.addError(target, this, "reg_exp.synatx.error", prop.getName(), regExp);
				}
			} else {
				try {
					if (!defaultValue.matches(regExp)) {
						PropertyMirror prop = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_reg_exp);
						context.addError(target, this, "reg.exp.violation", prop.getName(), regExp);
					}
				} catch (PatternSyntaxException e) {
					PropertyMirror prop = ((ClassMirror) target.getMeta().reflect()).getClassPropertyMirror(StringAttributeState.p_reg_exp);
					context.addError(target, this, "reg.exp.synatx.error", prop.getName(), regExp);
				}
			}
		}
	}

}
