package org.ink.language.example.videostore;

public class RestrictedVideotapeImpl<S extends RestrictedVideotapeState> extends VideotapeImpl<S> implements RestrictedVideotape {

	@Override
	public boolean canRent(CustomerState customer) {
		RestrictedMovie meta = getMeta();
		int minimumAge = meta.getMinimumAge();
		int customerAge = customer.getAge();
		return super.canRent(customer) && customerAge >= minimumAge;
	}
}