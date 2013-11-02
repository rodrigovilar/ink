package org.ink.core.vm.lang;

import org.ink.core.vm.constraints.PropertyConstraintsState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.property.mirror.PropertyMirrorState;

/**
 * @author Lior Schachter
 */
@CoreClassSpec(mirrorClass = PropertyMirrorState.class, constraintsClass = PropertyConstraintsState.class, isAbstract = true)
public interface PropertyState extends TypedObjectState {

	@CoreField(mandatory = true)
	public static final byte p_name = 1;
	@CoreField(defaultValue = "false")
	public static final byte p_mandatory = 2;
	public static final byte p_display_name = 3;

	// TODO should be mandatory true
	@CoreField(mandatory = false, valuePropagationStrategy = InheritanceConstraints.INSTANCE_MUST_OVERRIDE_INHERITED_VALUE)
	public static final byte p_description = 4;
	@CoreField(defaultValue = "Instance_Can_Refine_Inherited_Value")
	public static final byte p_inheritance_constraints = 5;

	public String getName();

	public void setName(String value);

	public Boolean getMandatory();

	public void setMandatory(Boolean value);

	public String getDescription();

	public void setDescription(String value);

	public String getDisplayName();

	public void setDisplayName(String value);

	public InheritanceConstraints getInheritanceConstraints();

	public void setInheritanceConstraints(InheritanceConstraints value);

	public class Data extends TypedObjectState.Data implements PropertyState {

		@Override
		public Boolean getMandatory() {
			return (Boolean) getValue(p_mandatory);
		}

		@Override
		public String getName() {
			return (String) getValue(p_name);
		}

		@Override
		public void setMandatory(Boolean value) {
			setValue(p_mandatory, value);
		}

		@Override
		public void setName(String value) {
			setValue(p_name, value);

		}

		@Override
		public String getDescription() {
			return (String) getValue(p_description);
		}

		@Override
		public void setDescription(String value) {
			setValue(p_description, value);
		}

		@Override
		public InheritanceConstraints getInheritanceConstraints() {
			return (InheritanceConstraints) getValue(p_inheritance_constraints);
		}

		@Override
		public void setInheritanceConstraints(InheritanceConstraints value) {
			setValue(p_inheritance_constraints, value);
		}

		@Override
		public String getDisplayName() {
			return (String) getValue(p_display_name);
		}

		@Override
		public void setDisplayName(String value) {
			setValue(p_display_name, value);
		}

	}
}
