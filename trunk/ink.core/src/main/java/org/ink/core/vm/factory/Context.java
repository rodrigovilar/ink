package org.ink.core.vm.factory;

import org.ink.core.vm.exceptions.CoreException;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Struct;
import org.ink.core.vm.traits.Trait;

/**
 * A <code>Context</code> is a trait of a {@link DslFactory}, responsible for lookup of existing Ink objects associated with the factory, and creation of new Ink objects.
 * <br/>
 * To retrieve a <code>Context</code> object, use {@link DslFactory#getAppContext()} or {@link VM#getContext()}.
 * <br/>
 * Example:
 * <pre>
 *     ActiveOffers offers = InkVM.instance().getContext().getObject("ink.tutorial1:Active_offers");
 *     bestOffer = offers.getBestOffer(...);
 * </pre>
 * @author Lior Schachter
 */
public interface Context extends Trait {

	/**
	 * Retrieves the namespace of this <code>Context</code>.
	 * @return the namespace of this <code>Context</code>.
	 */
	public String getNamespace();

	/**
	 * Creates a new {@link InkObjectState} of the required class. The instance will be assigned with a random unique ID, and its default values will be initialized.
	 * @param classId the ID of an Ink class.
	 * @return a new instance of the required class.
	 */
	public <T extends InkObjectState> T newInstance(String classId);

	/**
	 * Creates a new {@link InkObjectState} of the required class. Allows the client to control whether the new instance's ID and default values should be initialized.
	 * @param classId the ID of an Ink class.
	 * @param initObjectId whether the new instance's ID should be initialized.
	 * @param initDefaults whether the new instance's default values should be initialized.
	 * @return a new instance of the required class.
	 */
	public <T extends InkObjectState> T newInstance(String classId, boolean initObjectId, boolean initDefaults);

	/**
	 * Retrieves an existing <code>struct</code> by ID.
	 * @param id the ID of an Ink <code>struct</code>.
	 * @return the <code>struct</code> object.
	 * @throws CoreException if the ID couldn't be found.
	 */
	public <T extends Struct> T getStruct(String id);

	/**
	 * Retrieves an existing behavior object by ID.
	 * @param id the ID of an Ink object.
	 * @return the behavior object.
	 * @throws CoreException if the ID couldn't be found.
	 */
	public <T extends InkObject> T getObject(String id);

	/**
	 * Registers the supplied object with this <code>Context</code>. Registered objects can later be looked up by ID.
	 * @param state the state of the object to register.
	 */
	public void register(InkObjectState state);

	/**
	 * Retrieves the {@link DslFactory} which is the subject of this <code>Context</code> trait.
	 * @return the {@link DslFactory} which is the subject of this <code>Context</code> trait.
	 */
	public DslFactory getFactory();

	/**
	 * Retrieves an existing state object by ID.
	 * @param id the ID of an Ink object.
	 * @return the state object.
	 * @throws CoreException if the ID couldn't be found.
	 */
	public <T extends InkObjectState> T getState(String id);

	/**
	 * Retrieves an existing state object by ID, optionally throwing an exception if the ID couldn't be found.
	 * @param id the ID of an Ink object.
	 * @param reportErrorIfNotExists should an exception be thrown if the ID couldn't be found.
	 * @return the state object, or <code>null</code> if the ID couldn't be found and <code>reportErrorIfNotExists</code> is <code>false</code>.
	 */
	public <T extends InkObjectState> T getState(String id, boolean reportErrorIfNotExists);

	/**
	 * Returns the state object of the supplied behavior object. This operation is restricted to top-level objects only.
	 * @param object a behavior object whose state should be retrieved.
	 * @return the behavior's state, or <code>null</code> if the behavior is not a top-level object.
	 */
	public <T extends InkObjectState> T getState(InkObject object);
}
