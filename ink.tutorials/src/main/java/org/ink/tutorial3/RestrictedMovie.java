package org.ink.tutorial3;

public interface RestrictedMovie extends Movie {

	public boolean canRent(CustomerState customer);
}