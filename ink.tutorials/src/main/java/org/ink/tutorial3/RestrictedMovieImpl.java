package org.ink.tutorial3;

public class RestrictedMovieImpl<S extends RestrictedMovieState> extends MovieImpl<S> implements RestrictedMovie {

	@Override
	public boolean canRent(CustomerState customer) {
		return getState().getMinimumAge() <= customer.getAge();
	}
}
