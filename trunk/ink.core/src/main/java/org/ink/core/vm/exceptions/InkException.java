package org.ink.core.vm.exceptions;

/**
 * @author Lior Schachter
 */
public class InkException extends Exception{

	public InkException(String msg){
		super(msg);
	}

	public InkException(String msg, Throwable e){
		super(msg, e);
	}

}
