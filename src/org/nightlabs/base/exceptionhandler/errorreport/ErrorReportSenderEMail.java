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

/**
 * @author Simon Lehmann - simon@nightlabs.de
 */

import java.util.Iterator;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import org.nightlabs.config.Config;

public class ErrorReportSenderEMail implements ErrorReportSender
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ErrorReportSenderEMail.class);

	public ErrorReportSenderEMail()
	{
		super();
	}

	/**
	 * @see org.nightlabs.exceptiontest.wizard.ErrorReportSender#sendErrorReport(org.nightlabs.exceptiontest.wizard.ErrorReport)
	 */
	public void sendErrorReport(ErrorReport errorReport)
	{
		try {
			ErrorReportEMailCfMod cfMod = (ErrorReportEMailCfMod) Config.sharedInstance().createConfigModule(ErrorReportEMailCfMod.class);		
			Properties props = new Properties();
			props.put("mail.host", cfMod.getSmtpHost());
//			props.put("mail.from", cfMod.getMailFrom());
			

			Session mailConnection = Session.getInstance(props, null);
			Message msg = new MimeMessage(mailConnection);
			Address mailFrom = new InternetAddress(cfMod.getMailFrom());
			//Address simon = new InternetAddress("simon@nightlabs.de", "Simon Lehmann");

			//msg.setContent();
			msg.setText(errorReport.toString());
			msg.setFrom(mailFrom);
			
			
//			for (int i= 0; i < cfMod.getMailTo().size(); i++)
//			{
//				msg.addRecipient(Message.RecipientType.TO, new InternetAddress((String)cfMod.getMailTo().get(i)));
//				System.out.println(cfMod.getMailTo().get(i));
//			}
//			Iterator it = cfMod.getMailTo().iterator();
//			while(it.hasNext())
//				msg.setRecipient(Message.RecipientType.TO, new InternetAddress((String)it.next()));
				
			for(Iterator it2 = cfMod.getMailTo().iterator(); it2.hasNext();)
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress((String)it2.next()));
			
			
			
			msg.setSubject(errorReport.getThrownException().getClass().getName() +  " " + errorReport.getTimeAsString());
			

			Transport.send(msg);
			System.out.println("Message was sent!");
		} catch (Exception e) {
			logger.fatal("Sending error report by eMail failed.", e);
		}
	}

}
