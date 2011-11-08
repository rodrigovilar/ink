package org.ink.core.vm.exceptions;

/**
 * @author Lior Schachter
 */
public class InkExcpetion extends Exception{

	public InkExcpetion(String msg){
		super(msg);
	}

	public InkExcpetion(String msg, Throwable e){
		super(msg, e);
	}

}
