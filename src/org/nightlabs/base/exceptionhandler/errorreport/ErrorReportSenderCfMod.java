/*
 * Created on 07.04.2005
 */
package org.nightlabs.base.exceptionhandler.errorreport;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 */
public class ErrorReportSenderCfMod extends ConfigModule
{
	private String errorReportSenderClass;
	
	public ErrorReportSenderCfMod()
	{
	}

	/**
	 * @see org.nightlabs.config.ConfigModule#init()
	 */
	public void init() throws InitException
	{
		super.init();
		if (errorReportSenderClass == null)
			errorReportSenderClass = ErrorReportSenderEMail.class.getName();
	}
	public String getErrorReportSenderClass()
	{
		return errorReportSenderClass;
	}
	public void setErrorReportSenderClass(String errorReportSenderClass)
	{
		this.errorReportSenderClass = errorReportSenderClass;
		setChanged();
	}
}
