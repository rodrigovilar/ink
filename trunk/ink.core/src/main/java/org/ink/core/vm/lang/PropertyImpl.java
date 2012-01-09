package org.ink.core.vm.lang;

/**
 * @author Lior Schachter
 */
public abstract class PropertyImpl<S extends PropertyState> extends TypedObjectImpl<S> implements Property {

	@Override
	public String getName() {
		return getState().getName();
	}

	@Override
	public String getDisplayName() {
		String result = getState().getDisplayName();
		if (result == null) {
			return getName();
		}
		return result;
	}

	public boolean isMandatory() {
		return getState().getMandatory();
	}

}
