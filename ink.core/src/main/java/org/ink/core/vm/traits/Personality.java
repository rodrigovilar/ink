package org.ink.core.vm.traits;

import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.mirror.ClassMirror;

/**
 * A personality is a set of traits.
 * 
 * @author Lior Schachter
 * @see org.ink.core.vm.traits.Trait
 */
public interface Personality extends InkObject {

	public Trait getTrait(byte index);

	public boolean hasRole(String role);

	public void bind(ClassMirror cls) throws WeaveException;

	public byte getTraitsCount();

	public <T extends Trait> T adapt(byte index, InkObjectState state, ClassMirror cMirror);
}
