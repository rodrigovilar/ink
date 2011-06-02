package org.ink.language.example.videostore;

public class RestrictedMovieImpl<S extends RestrictedMovieState> extends MovieImpl<S> implements RestrictedMovie {

	@Override
	public int getMinimumAge() {
		return getState().getMinimumAge();
	}
}
