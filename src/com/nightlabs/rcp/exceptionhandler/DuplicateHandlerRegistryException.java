/*
 * Created 	on Mar 24, 2005
 * 					by alex
 *
 */
package com.nightlabs.rcp.exceptionhandler;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class DuplicateHandlerRegistryException extends RuntimeException {

	/**
	 * 
	 */
	public DuplicateHandlerRegistryException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DuplicateHandlerRegistryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DuplicateHandlerRegistryException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DuplicateHandlerRegistryException(Throwable cause) {
		super(cause);
	}
}
