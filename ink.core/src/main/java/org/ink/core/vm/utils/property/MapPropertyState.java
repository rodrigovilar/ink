package org.ink.core.vm.utils.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.JavaMapping;
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

	@CoreField(mandatory=true)
	public static final byte p_specifications = p_upper_bound+1;

	public Dictionary getSpecifications();
	public void setSpecifications(DictionaryState value);

	public class Data extends CollectionPropertyState.Data implements MapPropertyState{

		@Override
		public Dictionary getSpecifications() {
			return (Dictionary)getValue(p_specifications);
		}

		@Override
		public void setSpecifications(DictionaryState value) {
			setValue(p_specifications, value);
		}

	}
}
