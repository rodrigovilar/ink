package org.ink.language.example.videostore;

public class RestrictedVideotapeImpl<S extends RestrictedVideotapeState> extends VideotapeImpl<S> implements Videotape {

	@Override
	public boolean canRent(CustomerState customer) {
		RestrictedMovie meta = getMeta();
		return super.canRent(customer) && meta.canRent(customer);
	}
}