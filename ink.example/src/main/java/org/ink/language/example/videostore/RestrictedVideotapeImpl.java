package org.ink.language.example.videostore;

public class RestrictedVideotapeImpl<S extends RestrictedVideotapeState> extends VideotapeImpl<S> implements RestrictedVideotape {

	@Override
	public boolean canRent(CustomerState customer) {
		int minimumAge = ((RestrictedMovie) getMeta()).getMinimumAge();
		int customerAge = customer.getAge();
		return super.canRent(customer) && customerAge >= minimumAge;
	}
}