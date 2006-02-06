package org.nightlabs.base.exceptionhandler;

public class SimpleExceptionHandler implements IExceptionHandler
{
	private String message = "";
	public SimpleExceptionHandler(String message)
	{
		this.message = message;
	}
	
	public void handleException(Thread thread, Throwable thrownException, Throwable triggerException)
	{
		DefaultErrorDialog.addError(DefaultErrorDialog.class, null, message, thrownException, triggerException);
	}
}
