package org.ink.core.vm.exceptions;

/**
 * @author Lior Schachter
 */
public class CoreException extends RuntimeException {

	public CoreException(String msg) {
		super(msg);
	}

	public CoreException(Throwable e) {
		super(e);
	}

	public CoreException(String msg, Throwable e) {
		super(msg, e);
	}

}
