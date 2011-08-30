package org.ink.core.vm.exceptions;

/**
 * @author Lior Schachter
 */
public class CompilationException extends Exception {
	
	public CompilationException(String msg){
		super(msg);
	}
	
	public CompilationException(String msg, Throwable e){
		super(msg, e);
	}
	
}
