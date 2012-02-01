package org.ink.core.vm.lang.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

public class InkException extends RuntimeException {

	private Throwable cause;

	public InkException(String msg) {
		super(msg);
	}

	public InkException(String msg, Throwable ex) {
		super(msg);
		this.cause = ex;
	}

	public Throwable getCause() {
		return (this.cause == this ? null : this.cause);
	}

	public String getMessage() {
		if (getCause() == null) {
			return super.getMessage();
		} else {
			return super.getMessage() + ".  Nested exception is "
					+ getCause().getClass().getName() + " : "
					+ getCause().getMessage();
		}
	}

	public void printStackTrace(PrintStream ps) {
		if (getCause() == null) {
			super.printStackTrace(ps);
		} else {
			ps.println(this);
			getCause().printStackTrace(ps);
		}
	}

	public void printStackTrace(PrintWriter pw) {
		if (getCause() == null) {
			super.printStackTrace(pw);
		} else {
			pw.println(this);
			getCause().printStackTrace(pw);
		}
	}

	public boolean contains(Class exClass) {
		if (exClass == null) {
			return false;
		}
		Throwable ex = this;
		while (ex != null) {
			if (exClass.isInstance(ex)) {
				return true;
			}
			if (ex instanceof InkException) {
				ex = ex.getCause();
			} else {
				ex = null;
			}
		}
		return false;
	}


}
