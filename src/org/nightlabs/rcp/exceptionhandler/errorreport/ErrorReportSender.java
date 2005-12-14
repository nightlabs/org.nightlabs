/*
 * Created on 04.04.2005
 */
package org.nightlabs.rcp.exceptionhandler.errorreport;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 */
public interface ErrorReportSender
{
	public void sendErrorReport(ErrorReport errorReport);
}
