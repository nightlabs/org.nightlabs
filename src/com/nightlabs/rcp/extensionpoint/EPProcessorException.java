/*
 * Created 	on Oct 28, 2004
 * 					by Alexander Bieber
 *
 */
package com.nightlabs.rcp.extensionpoint;

/**
 * @author Alexander Bieber
 */
public class EPProcessorException extends Exception {

	/**
	 * 
	 */
	public EPProcessorException() {
		super();
	}

	/**
	 * @param message
	 */
	public EPProcessorException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EPProcessorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EPProcessorException(String message, Throwable cause) {
		super(message, cause);
	}

}
