package org.nightlabs.test;

public class MyException extends Exception {

	private static final long serialVersionUID = 1L;

	public MyException() {
	}

	public MyException(String message, Throwable cause) {
		super(message, cause);
	}

	public MyException(String message) {
		super(message);
	}

	public MyException(Throwable cause) {
		super(cause);
	}
}
