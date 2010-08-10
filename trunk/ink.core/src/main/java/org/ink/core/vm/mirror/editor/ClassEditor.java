package org.ink.core.vm.mirror.editor;

import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.lang.ObjectFactoryState;
import org.ink.core.vm.traits.Trait;
import org.ink.core.vm.traits.TraitClass;


/**
 * @author Lior Schachter
 */
public interface ClassEditor extends ObjectEditor{
	
	public void setFactory(ObjectFactoryState factoryState);
	public void weaveDetachableTrait(Trait trait) throws WeaveException;
	public void weaveStructuralTrait(String role, TraitClass traitClass) throws WeaveException;
}