package org.ink.core.vm.exceptions;

/**
 * @author Lior Schachter
 */
public class InkBootException extends RuntimeException{

	public InkBootException(String msg){
		super(msg);
	}
	
	public InkBootException(String msg, Throwable e){
		super(msg, e);
	}
	
}
