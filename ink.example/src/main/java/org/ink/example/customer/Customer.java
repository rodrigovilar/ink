package org.ink.example.customer;

import org.ink.core.vm.lang.InkObject;

/**
 * @author Lior Schachter
 */
public interface Customer extends InkObject{

	public boolean isFriend(Customer otherCustomer);
	public String sendLetter(String text);
	public String getFirstName();
	
}
