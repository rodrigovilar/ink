package org.ink.tutorial2;

public class Customer implements A_Customer {

	String name;
	String email;
	String creditCardNumber;
	boolean isStudent;
	
	public Customer(String name, String email, String creditCardNumber, boolean isStudent) {
		this.name = name;
		this.email = email;
		this.creditCardNumber = creditCardNumber;
		this.isStudent = isStudent;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getCreditCardNumber() {
		return creditCardNumber;
	}
	
	@Override
	public boolean isStudent() {
		return isStudent;
	}
	
}
