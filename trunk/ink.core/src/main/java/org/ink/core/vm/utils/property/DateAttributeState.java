package org.ink.core.vm.utils.property;

import java.util.Date;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirrorState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=PrimitiveAttributeMirrorState.class, constraintsClass=PropertyConstraintsState.class,
		finalValuesLocation={PropertyState.p_type}, finalValues={"ink.core:Date"})
public interface DateAttributeState extends PrimitiveAttributeState{

	@CoreField(mandatory=false)
	public static final byte p_default_value = 7;
	@CoreField(mandatory=false)
	public static final byte p_final_value = 8;
	@CoreField(mandatory=false)
	public static final byte p_date_pattern = 9;
	
	public Date getDefaultValue();
	public void setDefaultValue(Date value);
	
	public Date getFinalValue();
	public void setFinalValue(Date value);
	
	public String getDatePattern();
	public void setDatePattern(String value);
	
	public class Data extends PrimitiveAttributeState.Data implements DateAttributeState{

		@Override
		public Date getDefaultValue() {
			return (Date)getValue(p_default_value);
		}
		
		@Override
		public void setDefaultValue(Date value){
			setValue(p_default_value, value);
		}

		@Override
		public Date getFinalValue() {
			return (Date)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(Date value) {
			setValue(p_final_value, value);
		}

		@Override
		public String getDatePattern() {
			return (String)getValue(p_date_pattern);
		}

		@Override
		public void setDatePattern(String value) {
			setValue(p_date_pattern, value);
		}
		
	}
}
