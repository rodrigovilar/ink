package org.ink.core.vm.lang;

import com.sun.jdi.PrimitiveType;

/**
 * Provides information about the property definition.
 * 
 * @author Lior Schachter
 */
public interface Property extends TypedObject {

	public static final byte UNBOUNDED_PROPERTY_INDEX = -1;

	/**
	 * Defines whether <code>null</code> value is forbidden.
	 * 	 * 
	 * @return <code>true</code> if <code>null</code> is forbidden, <code>false</code> otherwise.
	 * 
	 */
	public boolean isMandatory();

	/**
	 * The property name.
	 * @return {@link String} - the prperty name.
	 */
	public String getName();

	/**
	 * User friendly property name.
	 * @return {@link String} - user friendly property name.
	 */
	public String getDisplayName();

	/**
	 * The type of the value to be stored in this property.
	 * 
	 * @return {@link InkType} - The data-type of the property.
	 */
	public InkType getType();

	/**
	 * Defines whether the property holds a single value or a collection.
	 * @return {@link Boolean} - <code>true</code> if collection, <code>false</code> otherwise.
	 */
	public boolean isPlural();

	/**
	 * Default value assigned to the property when a new instance of the containing class is created.
	 * 
	 * @return {@link Object} - The default value.  
	 */
	public Object getDefaultValue();

	/**
	 * The final value assigned to the property.  A final value is defined at the class level and can not be overriden by inheritance.
	 * @return {@link Object} - The final value or <code>null</code> in case there is no final value.
	 */
	public Object getFinalValue();
}
