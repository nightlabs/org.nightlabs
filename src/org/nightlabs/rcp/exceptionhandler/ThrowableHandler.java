/*
 * Created on 05.04.2005
 */
package org.nightlabs.rcp.exceptionhandler;

import org.apache.log4j.Logger;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ThrowableHandler implements IExceptionHandler
{
	public static final Logger LOGGER = Logger.getLogger(ThrowableHandler.class);
  
	/**
	 * @see org.nightlabs.rcp.exceptionhandler.IExceptionHandler#handleException(java.lang.Thread, java.lang.Throwable, java.lang.Throwable)
	 */
	public void handleException(Thread thread, Throwable thrownException,	Throwable triggerException)
	{
		try {
			LOGGER.error("ThrowableHandler handling an error!", thrownException);
      DefaultErrorDialog dlg = new DefaultErrorDialog(thrownException, triggerException);
      dlg.open();
		} catch (Throwable error) {
			LOGGER.fatal("While handling an exception, another one occured!", error);
		}
	}
}
