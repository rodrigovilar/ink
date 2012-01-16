package org.ink.core.vm.proxy;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.traits.Trait;

/**
 * 
 * Proxiability of a class is enabled by the class implementing <code>Proxiable</code> interface.
 * <br/>
 * Classes that do not implement this interface will not have their state proxied.
 * <br/>
 * All subtypes of a proxiable class are themselves proxiable.
 * <br/>
 * The <code>Proxiable</code> interface declares a predicate named {@link #isProxied}, which returns a boolean value.
 */
public interface Proxiable {

	/**
	 * The kind of proxiable object (state or behavior).
	 */
	public enum Kind {
		State, Behavior;
	}

	/**
	 * Returns the kind of proxiable object (state or behavior).
	 * @return the kind of proxiable object (state or behavior).
	 */
	public Kind getObjectKind();

	/**
	 * Returns <code>true</code> if this object is actually a proxy.
	 * @return <code>true</code> if this object is a proxy, <code>false</code> otherwise.
	 */
	public boolean isProxied();

	/**
	 * Cast this object as a trait. A trait is a part of an object's personality.
	 * <br/>
	 * Example:
	 * <pre>
	 *     public static final byte t_reflection = 0;
	 *     Trait trait = this.asTrait(t_reflection);
	 * 	   InkObject object = trait.getTargetBehavior();
	 * </pre>
	 * 
	 * @param index an absolute index of the trait.
	 * @return the <i>i</i>-th trait.
	 * @see org.ink.core.vm.traits.Trait
	 * @see org.ink.core.vm.lang.InkObjectImpl#asTrait(byte)
	 * @see InkObjectState#getBehavior()
	 * @throws ArrayIndexOutOfBoundsException if the index is invalid.
	 */
	public <T extends Trait> T asTrait(byte index);

	/**
	 * Cast this object as a trait. A trait is a part of an object's personality.
	 * <br/>
	 * Example:
	 * <pre>
	 *     public static final byte t_reflection = 0;
	 *     Trait trait = this.asTrait("reflection");
	 *     InkObject object = trait.getTargetBehavior();
	 * </pre>
	 * 
	 * @param role the role of the trait (as defined in this object's class) to which it should be cast.
	 * @return this object cast to the required trait, or <code>null</code> if the role wasn't found.
	 */
	public <T extends Trait> T asTrait(String role);

	/**
	 * Returns the Ink class of this object.
	 * 
	 * @return the Ink class of this object.
	 */
	public <M extends InkClass> M getMeta();

	/**
	 * Reifies (reflects) and return this object's mirror.
	 * 
	 * @return the mirror meta-object for this object.
	 */
	public <M extends Mirror> M reflect();
}
