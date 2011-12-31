package org.ink.tutorial3;

import org.ink.core.vm.lang.InkObjectImpl;

public class VideotapeImpl<S extends VideotapeState> extends InkObjectImpl<S> implements Videotape {

	@Override
	public boolean isRented() {
		return getState().getIsRented();
	}

	@Override
	public boolean canRent(CustomerState customer) {
		return !isRented();
	}
}