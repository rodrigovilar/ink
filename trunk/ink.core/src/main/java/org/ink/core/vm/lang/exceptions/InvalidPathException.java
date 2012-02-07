package org.ink.core.vm.lang.exceptions;

public class InvalidPathException extends InkException {

	public InvalidPathException(String msg) {
		super(msg);
	}

	public InvalidPathException(String msg, Exception e) {
		super(msg, e);
	}

}
