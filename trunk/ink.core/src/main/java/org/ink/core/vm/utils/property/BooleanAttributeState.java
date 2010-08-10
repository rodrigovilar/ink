package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=PrimitiveAttributeMirrorState.class, constraintsClass=PropertyConstraintsState.class, 
		finalValuesLocation={PropertyState.p_type}, finalValues={"ink.core:Boolean"})
public interface BooleanAttributeState extends PrimitiveAttributeState{

	@CoreField(mandatory=false)
	public static final byte p_default_value = 7;
	@CoreField(mandatory=false)
	public static final byte p_final_value = 8;
	
	public Boolean getDefaultValue();
	public void setDefaultValue(Boolean value);
	
	public Boolean getFinalValue();
	public void setFinalValue(Boolean value);
	
	public class Data extends PrimitiveAttributeState.Data implements BooleanAttributeState{

		@Override
		public Boolean getDefaultValue() {
			return (Boolean)getValue(p_default_value);
		}
		
		public void setDefaultValue(Boolean value){
			setValue(p_default_value, value);
		}

		@Override
		public Boolean getFinalValue() {
			return (Boolean)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(Boolean value) {
			setValue(p_final_value, value);
		}
	}
}