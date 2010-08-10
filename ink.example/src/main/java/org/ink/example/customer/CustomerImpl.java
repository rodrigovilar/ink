package org.ink.example.customer;

import org.ink.core.vm.lang.InkObjectImpl;

/**
 * @author Lior Schachter
 */
public class CustomerImpl extends InkObjectImpl<CustomerState> implements Customer{

	@Override
	public void sendLetter(String text) {
		System.out.println("To :" + getState().getFirstName() +" " + getState().getLastName());
		System.out.println(getState().getAddress().getCity() +" " + getState().getAddress().getStreet()+
				" " + getState().getAddress().getNumber());
		System.out.println(text);
	}

}
