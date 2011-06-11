package org.ink.language.example.videostore;

public interface RestrictedMovie extends Movie {

	public boolean canRent(CustomerState customer);
}