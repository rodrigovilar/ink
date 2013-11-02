package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.property.CollectionPropertyState;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass = ListPropertyMirrorState.class, constraintsClass = PropertyConstraintsState.class, javaMapping = JavaMapping.STATE_BEHAVIOR, finalValuesLocation = { PropertyState.p_type }, finalValues = { "ink.core:List" })
public interface ListPropertyState extends CollectionPropertyState {

	@CoreField(mandatory = true)
	public static final byte p_list_item = 8;

	public Property getListItem();

	public void setListItem(PropertyState value);

	public class Data extends CollectionPropertyState.Data implements ListPropertyState {

		@Override
		public Property getListItem() {
			return (Property) getValue(p_list_item);
		}

		@Override
		public void setListItem(PropertyState value) {
			setValue(p_list_item, value);
		}
	}
}
