package org.ink.language.example.videostore;

import org.ink.core.vm.lang.InkClassImpl;

public class MovieImpl<S extends MovieState> extends InkClassImpl<S> implements Movie {

	@Override
	public String getTitle() {
		return getState().getTitle();
	}

	@Override
	public String getRating() {
		return getState().getRating();
	}
}
