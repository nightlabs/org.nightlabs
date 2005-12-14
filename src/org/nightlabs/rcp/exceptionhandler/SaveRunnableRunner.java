/*
 * Created 	on Sep 4, 2005
 * 					by alex
 *
 */
package org.nightlabs.rcp.exceptionhandler;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.util.ISafeRunnableRunner;

/**
 * Runner for <tt>ISafeRunnable</tt>s that uses
 * the {@link org.nightlabs.rcp.exceptionhandler.ExceptionHandlerRegistry}.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SaveRunnableRunner implements ISafeRunnableRunner {

	public SaveRunnableRunner() {
		super();
	}

	/**
	 * Runs the code and handles exceptions with
	 * the {@link ExceptionHandlerRegistry}.
	 * 
	 * @see org.eclipse.jface.util.ISafeRunnableRunner#run(org.eclipse.core.runtime.ISafeRunnable)
	 */
	public void run(ISafeRunnable code) {
		try {
			code.run();
		} catch (Throwable t) {
			ExceptionHandlerRegistry.asyncHandleException(t);
		}
	}

}
