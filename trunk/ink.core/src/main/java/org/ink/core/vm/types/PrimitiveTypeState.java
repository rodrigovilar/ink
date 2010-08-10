package org.ink.core.vm.types;

import org.ink.core.vm.lang.InkTypeState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(javaMapping=JavaMapping.State_Behavior_Interface)
public interface PrimitiveTypeState extends InkTypeState{

	public static final byte p_type_marker = 0;
	
	public PrimitiveTypeMarker getTypeMarker();
	public void setTypeMarker(PrimitiveTypeMarker value);
	
	public class Data extends InkTypeState.Data implements PrimitiveTypeState{

		@Override
		public PrimitiveTypeMarker getTypeMarker() {
			return (PrimitiveTypeMarker) getValue(p_type_marker);
		}

		@Override
		public void setTypeMarker(PrimitiveTypeMarker value) {
			setValue(p_type_marker, value);
		}
		
		
	}

}
