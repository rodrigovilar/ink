package org.ink.core.vm.lang;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.DslFactory;

/**
 * The interface of the base Ink meta-class. Every Ink class is an <i>instance</i> of this class. Every Ink meta-class extends this class
 * (explicitly or implicitly).
 * <br/>
 * Since instances of this meta-class are classes, their main responsibility is to create and initialize new instances. The instances are
 * created with the help of an {@link ObjectFactory}.
 * <p>In Java, each Ink meta-class is represented as a Java class. Each Ink class corresponds to a Java <i>object</i>, which is an instance
 * of that class.</p>
 * <p>Example:
 * <br/>
 * In Ink, there's a class <code>ink.example:MyClass</code>, which is an instance of the meta-class <code>ink.example:MyMetaClass</code>.
 * In Java, there exist classes (or interfaces) <code>org.ink.example.MyMetaClass</code> and <code>org.ink.example.MyClass</code>, and the
 * following holds:
 * <pre>
 *   MyMetaClass myClass = context.getObject("ink.example:MyClass"); // Returns the object representing MyClass. That object is
 *                                                                   // an instance of MyMetaClass.
 * 
 *   MyClass myObject = myClass.newInstance();                       // Creates a new instance of MyClass.
 * 
 *   MyClass myObject2 = context.newInstance("ink.example:MyClass"); // An equivalent way to create a new instance of MyClass.
 * </pre>
 * </p>
 * 
 * @author Lior Schachter
 */
public interface InkClass extends InkType {

	/**
	 * Creates a new instance of this Ink class. Object creation in Ink is performed via one of the <code>newInstance</code>
	 * methods, instead of the Java <code>new</code> operator.
	 * <br/>
	 * The new instance is created with the context that loaded the class object. The object's ID is not initialized.
	 * Default values are initialized.
	 * @return a new instance of this Ink class.
	 */
	public <T extends InkObjectState> T newInstance();

	/**
	 * Creates a new instance of this Ink class with the specified context. Object creation in Ink is performed via one
	 * of the <code>newInstance</code> methods, instead of the Java <code>new</code> operator.
	 * <br/>
	 * The object's ID is not initialized. Default values are initialized.
	 * @param context the context with which the new instance will be created.
	 * @return a new instance of this Ink class.
	 */
	public <T extends InkObjectState> T newInstance(Context context);

	/**
	 * Creates a new instance of this Ink class, initializing the object ID and default values if required.
	 * Object creation in Ink is performed via one of the <code>newInstance</code> methods, instead of the Java
	 * <code>new</code> operator.
	 * <br/>
	 * The new instance is created with the context that loaded the class object.
	 * @param initObjectId whether the object's ID should be initialized.
	 * @param initDefaults whether default values should be initialized.
	 * @return a new instance of this Ink class.
	 */
	public <T extends InkObjectState> T newInstance(boolean initObjectId, boolean initDefaults);

	/**
	 * Creates a new instance of this Ink class with the specified context, initializing the object ID and default values if required.
	 * Object creation in Ink is performed via one of the <code>newInstance</code> methods, instead of the Java
	 * <code>new</code> operator.
	 * @param context the context with which the new instance will be created.
	 * @param initObjectId whether the object's ID should be initialized.
	 * @param initDefaults whether default values should be initialized.
	 * @return a new instance of this Ink class.
	 */
	public <T extends InkObjectState> T newInstance(Context context, boolean initObjectId, boolean initDefaults);

	/**
	 * Creates a new instance of this Ink class with the specified {@link DslFactory}, initializing the object ID and default values
	 * if required. Object creation in Ink is performed via one of the <code>newInstance</code> methods, instead of the Java
	 * <code>new</code> operator.
	 * @param factory the {@link DslFactory} with which the new instance will be created.
	 * @param initObjectId whether the object's ID should be initialized.
	 * @param initDefaults whether default values should be initialized.
	 * @return a new instance of this Ink class.
	 */
	public <T extends InkObjectState> T newInstance(DslFactory factory, boolean initObjectId, boolean initDefaults);

	/**
	 * Initializes the provided instance of this class, initializing the object ID and default values if required.
	 * <br/>
	 * Application classes may override this method and provide their own initialization logic.
	 * @param state a new instance of this class to be initialized.
	 * @param initObjectId whether the object's ID should be initialized.
	 * @param initDefaults whether default values should be initialized.
	 */
	public void initInstance(InkObjectState state, boolean initObjectId, boolean initDefaults);
}
