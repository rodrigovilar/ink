package org.ink.core.vm.factory;

import org.ink.core.vm.lang.InkClass;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Struct;
import org.ink.core.vm.traits.Trait;

/**
 * @author Lior Schachter
 */
public interface Context extends Trait{
	
	public String getNamespace();
	public <T extends InkObjectState> T newInstance(Class<T> stateClass);
	public <T extends InkObjectState> T newInstance(Class<T> stateClass, boolean initObjectId, boolean initDefaults);
	public <T extends Struct> T getStruct(String id);
	public <T extends InkObject> T getObject(String id);
	public <T extends InkClass> T getObject(Class<InkObjectState> stateClass);
	public void register(InkObjectState state);
	public void register(InkObject o);
	public DslFactory getFactory();
	public <T extends InkObjectState> T getState(String id);
	public <T extends InkObjectState> T getState(InkObject object);
}
