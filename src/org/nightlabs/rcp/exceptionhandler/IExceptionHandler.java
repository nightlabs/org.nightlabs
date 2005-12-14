/*
 * Created on Sep 23, 2004
 */
package org.nightlabs.rcp.exceptionhandler;

/**
 * Simple Interface for exception handling. Implement this to extend
 * extension-point "org.nightlabs.ipanema.rcp.eventloopexceptionhandler".
 *  
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de> 
 *
 */
public interface IExceptionHandler {
	/**
	 * Method called to handle uncaught exceptions.
	 * It will always be executed on the GUI thread.
	 *  
	 * @param thread The thread the exception was thrown on.
	 * @param thrownException The Exception actually thrown.
	 * @param triggerException The Exception exception that caused the caller 
	 *                         to pick this particular handler so this always should be
	 *                         the Exception the handler was registered on 
	 */
	public void handleException(Thread thread, Throwable thrownException, Throwable triggerException);
}
