/*
 * Created on 04.04.2005
 */
package com.nightlabs.rcp.exceptionhandler.errorreport;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 */

import java.util.Iterator;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.nightlabs.config.Config;

public class ErrorReportSenderEMail implements ErrorReportSender
{
	public static final Logger LOGGER = Logger.getLogger(ErrorReportSenderEMail.class);

	public ErrorReportSenderEMail()
	{
		super();
	}

	/**
	 * @see com.nightlabs.exceptiontest.wizard.ErrorReportSender#sendErrorReport(com.nightlabs.exceptiontest.wizard.ErrorReport)
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
			LOGGER.fatal("Sending error report by eMail failed.", e);
		}
	}

}
