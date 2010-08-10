package org.ink.core.vm.exceptions;

/**
 * @author Lior Schachter
 */
public class WeaveException extends Exception {
	
	public WeaveException(String msg){
		super(msg);
	}
	
	public WeaveException(String msg, Throwable e){
		super(msg, e);
	}
	
}
