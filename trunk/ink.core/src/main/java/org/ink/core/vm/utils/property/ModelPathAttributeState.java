package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CorePropertySpec;
import org.ink.core.vm.utils.property.constraints.ModelPathAttributeValueValidatorState;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass = PrimitiveAttributeMirrorState.class, constraintsClass = PropertyConstraintsState.class, finalValuesLocation = { PropertyState.p_type }, finalValues = { "ink.core:String" })
@CorePropertySpec(keys = { "modelpath_value_validator" }, validatorsClasses = {ModelPathAttributeValueValidatorState.class })
public interface ModelPathAttributeState extends PrimitiveAttributeState {

	@CoreField(mandatory = false)
	public static final byte p_default_value = 7;
	@CoreField(mandatory = false)
	public static final byte p_final_value = 8;

	public String getDefaultValue();

	public void setDefaultValue(String value);

	public String getFinalValue();

	public void setFinalValue(String value);
	

	public class Data extends PrimitiveAttributeState.Data implements ModelPathAttributeState {

		@Override
		public String getDefaultValue() {
			return (String) getValue(p_default_value);
		}

		@Override
		public void setDefaultValue(String value) {
			setValue(p_default_value, value);
		}

		@Override
		public String getFinalValue() {
			return (String) getValue(p_final_value);
		}

		@Override
		public void setFinalValue(String value) {
			setValue(p_final_value, value);
		}

	}
}
