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
	@CoreField(defaultValue="item")
	public static final byte p_item_name = 8;
	@CoreField(mandatory=true)
	public static final byte p_map_value = 9;
	@CoreField(mandatory=true)
	public static final byte p_map_key = 10;

	public Property getMapValue();
	public void setMapValue(PropertyState value);

	public Property getMapKey();
	public void setMapKey(PropertyState value);

	public String getItemName();
	public void setItemName(String value);

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
		public String getItemName() {
			return (String)getValue(p_item_name);
		}

		@Override
		public void setItemName(String value) {
			setValue(p_item_name, value);
		}
	}
}
