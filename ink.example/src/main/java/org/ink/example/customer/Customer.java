package org.ink.example.customer;

import org.ink.core.vm.lang.InkObject;

/**
 * @author Lior Schachter
 */
public interface Customer extends InkObject{

	public void sendLetter(String text);
	
}
