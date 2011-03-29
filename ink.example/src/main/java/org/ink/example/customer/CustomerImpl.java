package org.ink.example.customer;

import org.ink.core.vm.lang.InkObjectImpl;

/**
 * @author Lior Schachter
 */
public class CustomerImpl extends InkObjectImpl<CustomerState> implements Customer{

	@Override
	public String sendLetter(String text) {
		System.out.println("To :" + getState().getFirstName() +" " + getState().getLastName());
		System.out.println(getState().getAddress().getCity() +" " + getState().getAddress().getStreet()+
				" " + getState().getAddress().getNumber());
		System.out.println(text);
		return text;
	}

	@Override
	public String getFirstName() {
		return getState().getFirstName();
	}

	@Override
	public boolean isFriend(Customer otherCustomer) {
		if(getState().getFriends()!=null){
			return getState().getFriends().contains(otherCustomer);
		}
		return false;
	}

}
