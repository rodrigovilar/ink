package org.ink.core.vm.traits;

import org.ink.core.vm.constraints.Constraints;
import org.ink.core.vm.constraints.ConstraintsState;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.MirrorState;

/**
 * @author Lior Schachter
 */
public interface PersonalityState extends InkObjectState {

	public static final byte p_reflection = 0;
	public static final byte p_constraints = 1;

	public Mirror getReflection();

	public void setReflection(MirrorState value);

	public Constraints getConstraints();

	public void setConstraints(ConstraintsState value);

	public class Data extends InkObjectState.Data implements PersonalityState {
		@Override
		public Mirror getReflection() {
			return (Mirror) getValue(p_reflection);
		}

		@Override
		public void setReflection(MirrorState value) {
			setValue(p_reflection, value);
		}

		@Override
		public Constraints getConstraints() {
			return (Constraints) getValue(p_constraints);
		}

		@Override
		public void setConstraints(ConstraintsState value) {
			setValue(p_constraints, value);
		}

	}

}
