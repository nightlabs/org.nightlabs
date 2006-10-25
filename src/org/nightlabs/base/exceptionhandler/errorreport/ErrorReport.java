/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.exceptionhandler.errorreport;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.nightlabs.io.DataBuffer;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReport
implements Serializable
{
	private Throwable thrownException;
	private Throwable triggerException;
	private String userComment;
	private Properties systemProperties;
	private Date time;

	/**
	 * Initialize an empty error report. 
	 */
	protected ErrorReport()
	{
	}

	/**
	 * Initialize this error report with a thrown and a trigger exception.
	 * @param thrownException The exception thrown
	 * @param triggerException The exception that triggered the error handler
	 */
	public ErrorReport(Throwable thrownException, Throwable triggerException)
	{
		setThrownException(thrownException);
		setTriggerException(triggerException);
		this.systemProperties = System.getProperties();
		this.time = new Date();
	}

	/**
	 * @return The thrownException.
	 */
	public Throwable getThrownException()
	{
		return thrownException;
	}

	/**
	 * @param error The thrownException to set.
	 */
	public void setThrownException(Throwable error)
	{
		if (error == null)
			throw new NullPointerException("Parameter thrownException must not be null!");
		this.thrownException = error;
	}

	/**
	 * @return The triggerException.
	 */
	public Throwable getTriggerException()
	{
		return triggerException;
	}

	/**
	 * @param triggerException The triggerException to set.
	 */
	public void setTriggerException(Throwable triggerException)
	{
		if (triggerException == null)
			throw new NullPointerException("Parameter triggerException must not be null!");
		this.triggerException = triggerException;
	}

	/**
	 * @return Returns the userComment.
	 */
	public String getUserComment()
	{
		return userComment;
	}

	/**
  public String getErrorStackTraceAsString(Throwable error)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    error.printStackTrace(pw);
    pw.close();
    return sw.getBuffer().toString();
  }


  public String getCurrentTimeAsString()
  {
    SimpleDateFormat bartDateFormat =
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String timestring = new String();
    Date date = new Date();
    timestring =bartDateFormat.format(date);
    return timestring;
  }

	 * @param userComment The userComment to set.
	 */
	public void setUserComment(String userComment)
	{
		this.userComment = userComment;
	}

	/**
	 * @return The system properties associated with this error report
	 */
	public Properties getSystemProperties()
	{
		return systemProperties;
	}

	/**
	 * @param systemProperties The system properties to associate with this error report
	 */
	public void setSystemProperties(Properties systemProperties)
	{
		this.systemProperties = systemProperties;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		if (time == null)
			throw new NullPointerException("Parameter time must not be null!");
		this.time = time;
	}

	protected String getTimeAsString()
	{
		return getTimeAsString(time);
	}

	/**
	 * Formats the thrownException report into sth. like this:
	 * 
	 * ---
	 * User Comment:
	 * bla bla bla
	 * 
	 * Error:
	 * java.lang.Exception: shfgiushf
	 *   at xxx
	 * ---
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer props = new StringBuffer();
		try {
			DataBuffer db = new DataBuffer(1024);
			OutputStream out = db.createOutputStream();
			systemProperties.storeToXML(out, "");
			out.close();
			InputStream in = db.createInputStream();
			InputStreamReader reader = new InputStreamReader(in, "UTF-8");
			while (reader.ready()) {
				props.append((char)reader.read());
			}
		} catch (Exception x) {
			props.append("Dumping system properties failed: " + x.getMessage());
		}

//		StringBuffer props = new StringBuffer();
//		for (Iterator it = systemProperties.entrySet().iterator(); it.hasNext(); ) {
//		Map.Entry me = (Map.Entry)it.next();
//		props.append(me.getKey());
//		props.append('=');
//		props.append(me.getValue());
//		props.append('\n');
//		}

		return
		"Time:\n"+ getTimeAsString() +"\n\nUser Comment:\n" + userComment + 
		"\n\nThrown exception stack trace:\n" + getExceptionStackTraceAsString(thrownException) +
		"\nSystem Properties:\n" + props.toString();
	}

	public static String getExceptionStackTraceAsString(Throwable exception)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		pw.close();
		return sw.getBuffer().toString();
	}

	public static String getTimeAsString(Date time)
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
	}
}
