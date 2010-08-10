package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.property.CollectionPropertyState;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirrorState;


/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass=MapPropertyMirrorState.class, constraintsClass=PropertyConstraintsState.class, 
		javaMapping=JavaMapping.State_Behavior, 
		finalValuesLocation={PropertyState.p_type}, finalValues={"ink.core:Map"})
public interface MapPropertyState extends CollectionPropertyState{
	
	public static final byte p_map_value = 8;
	public static final byte p_map_key = 9;
	@CoreField(defaultValue="false")
	public static final byte p_is_sorted = 10;
	
	public Property getMapValue();
	public void setMapValue(PropertyState value);
	
	public Property getMapKey();
	public void setMapKey(PropertyState value);
	
	public Boolean getIsSorted();
	public void setIsSorted(Boolean value);
	
	public class Data extends CollectionPropertyState.Data implements MapPropertyState{

		@Override
		public Property getMapValue() {
			return (Property)getValue(p_map_value);
		}

		@Override
		public void setMapValue(PropertyState value) {
			setValue(p_map_value, value);
		}
		
		@Override
		public Property getMapKey() {
			return (Property)getValue(p_map_key);
		}

		@Override
		public void setMapKey(PropertyState value) {
			setValue(p_map_key, value);
		}

		@Override
		public Boolean getIsSorted() {
			return (Boolean)getValue(p_is_sorted);
		}

		@Override
		public void setIsSorted(Boolean value) {
			setValue(p_is_sorted, value);
		}
	}
}
