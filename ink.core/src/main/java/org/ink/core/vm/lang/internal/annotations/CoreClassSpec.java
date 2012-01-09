package org.ink.core.vm.lang.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ink.core.vm.constraints.ConstraintsState;
import org.ink.core.vm.constraints.InstanceValidatorState;
import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.JavaMapping;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.constraints.GenericInstanceValidatorState;
import org.ink.core.vm.mirror.MirrorState;
import org.ink.core.vm.traits.PersonalityState;

/**
 * @author Lior Schachter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CoreClassSpec {
	public Scope scope() default Scope.all;

	public boolean isAbstract() default false;

	public Class<?> metaclass() default InkClassState.class;

	public Class<? extends MirrorState> mirrorClass() default MirrorState.class;

	public Class<? extends ConstraintsState> constraintsClass() default ConstraintsState.class;

	public Class<? extends GenericInstanceValidatorState> genericValidatorClass() default GenericInstanceValidatorState.class;

	public Class<? extends PersonalityState> traitsClass() default PersonalityState.class;

	public JavaMapping javaMapping() default JavaMapping.State_Behavior_Interface;

	public byte[] finalValuesLocation() default {};

	public String[] finalValues() default {};

	public String[] validatorsKeys() default {};

	public Class<? extends InstanceValidatorState>[] validatorsClasses() default {};

}
