package org.ink.core.vm.lang.property;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.PropertyState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirrorState;
import org.ink.core.vm.types.CollectionType;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass = CollectionPropertyMirrorState.class, constraintsClass = PropertyConstraintsState.class, isAbstract = true)
public interface CollectionPropertyState extends PropertyState {

	@CoreField(mandatory = false)
	public static final byte p_lower_bound = p_inheritance_constraints + 1;
	@CoreField(mandatory = false)
	public static final byte p_upper_bound = p_lower_bound + 1;

	@Override
	public CollectionType getType();

	public Integer getLowerBound();

	public void setLowerBound(Integer value);

	public Integer getUpperBound();

	public void setUpperBound(Integer value);

	public class Data extends PropertyState.Data implements CollectionPropertyState {

		@Override
		public Integer getLowerBound() {
			return (Integer) getValue(p_lower_bound);
		}

		@Override
		public void setLowerBound(Integer value) {
			setValue(p_lower_bound, value);
		}

		@Override
		public Integer getUpperBound() {
			return (Integer) getValue(p_upper_bound);
		}

		@Override
		public void setUpperBound(Integer value) {
			setValue(p_upper_bound, value);
		}

		@Override
		public CollectionType getType() {
			return (CollectionType) getValue(p_type);
		}

	}
}
