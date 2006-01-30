package org.nightlabs.base.exceptionhandler;

public class ErrorItem
{
	private Throwable thrownException, triggerException;
	private String message;
	
	public ErrorItem(String message, Throwable thrownException, Throwable triggerException)
	{
		this.message = message;
		this.thrownException = thrownException;
		this.triggerException = triggerException;
	}
	
	public String getMessage()
	{
		return message;
	}
	public Throwable getThrownException()
	{
		return thrownException;
	}
	public Throwable getTriggerException()
	{
		return triggerException;
	}
}
