package org.ink.core.vm.lang;

import org.ink.core.vm.constraints.InstanceValidator;
import org.ink.core.vm.constraints.PropertyValueValidator;
import org.ink.core.vm.constraints.SystemState;
import org.ink.core.vm.constraints.ValidationContext;
import org.ink.core.vm.constraints.Validator;
import org.ink.core.vm.mirror.editor.ObjectEditor;
import org.ink.core.vm.proxy.Proxiable;

/**
 * The interface of the base Ink behavior class. Every behavior class in Ink implements this interface (explicitly or via inheritance).
 * 
 * @see InkObjectImpl
 * @see InkObjectState
 * 
 * @author Lior Schachter
 */
public interface InkObject extends Proxiable {

	/**
	 * A hook method, notifying the behavior object that the state object has been created (or modified externally). This method is a rough
	 * equivalent of a constructor method in Java. However, note that it may be invoked multiple times during the lifecycle
	 * of a behavior object: when a behavior is first created and injected with its state object, and after the state object has been modified
	 * (e.g., by an {@link ObjectEditor}).
	 * <br/>
	 * <b>This method should contain initialization logic of the behavior object.</b>
	 */
	public void afterStateSet();

	/**
	 * Creates a deep clone of this behavior object's state. The clone operation traverses the state object and creates a clone of every mutable
	 * object it encounters, such as inner state objects and collections. (Primitive values are immutable and therefore are shallow-copied.)
	 * <br/>
	 * Object IDs are not cloned. The cloned state object and all its inner objects have unique IDs. Behavior objects are not created during this
	 * operation.
	 * @return a deep clone of this behavior object's state.
	 */
	public <T extends InkObjectState> T cloneState();

	/**
	 * Validates this object by executing the validators from the <code>constraints</code> trait relevant to the required system state.
	 * The messages reported during validation are collected into the context object.
	 * <br/>
	 * @param context a {@link ValidationContext} object into which the validation messages are collected.
	 * @param systemState denotes whether design-time or run-time validators should be invoked.
	 * @return <code>true</code> if no validation errors were encountered, <code>false</code> otherwise.
	 * @see Validator
	 * @see InstanceValidator#validate(InkObjectState, org.ink.core.vm.mirror.Mirror, ValidationContext, SystemState)
	 * @see PropertyValueValidator#validate(Property, Object, InkObjectState, ValidationContext, SystemState)
	 */
	public boolean validate(ValidationContext context, SystemState systemState);

	/**
	 * Validates this object by executing the validators from the <code>constraints</code> trait relevant to run-time system state.
	 * Equivalent to calling <code>validate(context, SystemState.Run_Time)</code>.
	 * The messages reported during validation are collected into the context object.
	 * <br/>
	 * @param context a {@link ValidationContext} object into which the validation messages are collected.
	 * @return <code>true</code> if no validation errors were encountered, <code>false</code> otherwise.
	 * @see Validator
	 * @see InstanceValidator#validate(InkObjectState, org.ink.core.vm.mirror.Mirror, ValidationContext, SystemState)
	 * @see PropertyValueValidator#validate(Property, Object, InkObjectState, ValidationContext, SystemState)
	 */
	public boolean validate(ValidationContext context);

}
