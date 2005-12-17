/*
 * Created 	on Oct 21, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.exceptionhandler;

import org.apache.log4j.Logger;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class OutOfMemoryHandler implements IExceptionHandler {
	
	private static final Logger LOGGER = Logger.getLogger(OutOfMemoryHandler.class);
	/**
	 * @see org.nightlabs.base.exceptionhandler.IExceptionHandler#handleException(java.lang.Thread, java.lang.Throwable, java.lang.Throwable)
	 */
	public void handleException(Thread thread, Throwable thrownException, Throwable triggerException) 
	{
		try {
			LOGGER.error("OutOfMemoryHandler handling an error!", thrownException);
      OutOfMemoryErrorDialog dlg = new OutOfMemoryErrorDialog(thrownException, triggerException);
      dlg.open();
		} catch (Throwable error) {
			LOGGER.fatal("While handling an exception, another one occured!", error);
		}
	}

}
