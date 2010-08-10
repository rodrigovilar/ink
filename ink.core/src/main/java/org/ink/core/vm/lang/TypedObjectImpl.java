package org.ink.core.vm.lang;

/**
 * @author Lior Schachter
 */
public abstract class TypedObjectImpl<S extends TypedObjectState> extends InkObjectImpl<S> implements TypedObject{

	@Override
	public InkType getType() {
		return getState().getType();
	}

}
