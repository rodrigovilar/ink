package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.property.ValuePropertyState;
import org.ink.core.vm.utils.property.mirror.ReferenceMirrorState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=ReferenceMirrorState.class, constraintsClass=PropertyConstraintsState.class)
public interface ReferenceState extends ValuePropertyState{
	
	public static final byte p_default_value = 7;
	public static final byte p_final_value = 8;
	public static final byte p_kind = 9;
	
	public InkObject getDefaultValue();
	public void setDefaultValue(InkObjectState value);
	
	public InkObject getFinalValue();
	public void setFinalValue(InkObjectState value);
	
	public ReferenceKind getKind();
	public void setKind(ReferenceKind value);
	public InkClass getType();
	
	public class Data extends ValuePropertyState.Data implements ReferenceState{

		@Override
		public InkClass getType() {
			return (InkClass) getValue(p_type);
		}
		
		public void setType(InkClassState value) {
			super.setType(value);
		}
		
		@Override
		public ReferenceKind getKind() {
			return (ReferenceKind)getValue(p_kind);
		}

		@Override
		public void setKind(ReferenceKind value) {
			setValue(p_kind, value);
		}
		
		@Override
		public InkObject getDefaultValue() {
			return (InkObject)getValue(p_default_value);
		}

		@Override
		public void setDefaultValue(InkObjectState value) {
			setValue(p_default_value, value);
		}

		@Override
		public InkObject getFinalValue() {
			return (InkObject)getValue(p_final_value);
		}

		@Override
		public void setFinalValue(InkObjectState value) {
			setValue(p_final_value, value);
		}


	}
}