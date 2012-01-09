package org.ink.core.vm.proxy;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.traits.Trait;

/**
 * 
 * Proxiability of a class is enabled by the class implementing the org.ink.core.vm.proxy.Proxiable interface.
 * Classes that do not implement this interface will not have their state proxied.
 * All subtypes of a proxiable class are themselves proxiable.
 * The Proxiable interface declares a predicate named isProxied, which returns a boolean value.
 * 
 */

public interface Proxiable {

	public enum Kind {
		State, Behavior;
	}

	public Kind getObjectKind();

	public boolean isProxied();

	/**
	 * Cast as a trait. A trait is a part of an object's personality.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {
	 * 	&#064;code
	 * 	public static final byte t_reflection = 0;
	 * }
	 * {
	 * 	&#064;code
	 * 	Trait trait = this.asTrait(t_reflection);
	 * }
	 * {
	 * 	&#064;code
	 * 	InkObject object = trait.getTargetBehavior();
	 * }
	 * </pre>
	 * 
	 * @param index
	 *            an absolute index of the trait
	 * @return the i-th trait
	 * @see org.ink.core.vm.traits.Trait
	 * @see org.ink.core.vm.lang.InkObjectImpl#asTrait(byte)
	 * @see InkObjectState#getBehavior()
	 * @since {@literal ink.core}
	 */
	public <T extends Trait> T asTrait(byte index);

	public <T extends Trait> T asTrait(String role);

	/**
	 * getInkClass. Should be renamed as getClass (not possible) or getKind or getInkClass ?
	 * 
	 * @return the class of this object
	 */
	public <M extends InkClass> M getMeta();

	/**
	 * reify (reflect) and return mirror
	 * 
	 * @param <M extends Mirror>
	 * @return the mirror meta-object for this object
	 */
	public <M extends Mirror> M reflect();
}
