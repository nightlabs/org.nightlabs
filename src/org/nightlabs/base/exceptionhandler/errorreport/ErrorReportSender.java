/*
 * Created on 04.04.2005
 */
package org.nightlabs.base.exceptionhandler.errorreport;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 */
public interface ErrorReportSender
{
	public void sendErrorReport(ErrorReport errorReport);
}
