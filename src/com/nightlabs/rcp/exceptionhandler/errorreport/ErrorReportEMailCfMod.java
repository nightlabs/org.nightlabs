/*
 * Created on 06.04.2005
 */
package com.nightlabs.rcp.exceptionhandler.errorreport;

import java.util.List;

import com.nightlabs.config.CfModList;
import com.nightlabs.config.ConfigModule;
import com.nightlabs.config.InitException;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 */
public class ErrorReportEMailCfMod extends ConfigModule
{
	private String mailFrom = null;
	private List mailTo = null;
	private String smtpHost = null;
	
	
	public ErrorReportEMailCfMod()
	{
	}
	
	/**
	 * @see com.nightlabs.config.ConfigModule#init()
	 */
	public void init() throws InitException
	{
		super.init();
		if (mailFrom == null)
			mailFrom = "ipanema@nightlabs.de";
		if (mailTo == null)
		{
			mailTo = new CfModList(this);
      mailTo.add("dev@nightlabs.de");
//			mailTo.add("simon@nightlabs.de");
//			mailTo.add("garbage@nightlabs.de");
//			mailTo.add("alex@nightlabs.de");
//			mailTo.add("nick@nightlabs.de");
//			mailTo.add("marco@nightlabs.de");

		}
		if (smtpHost == null)
			smtpHost = "mail.nightlabs.de";
	}
	
	public String getMailFrom()
	{
		return mailFrom;
	}
	public void setMailFrom(String mailFrom)
	{
		this.mailFrom = mailFrom;
		setChanged();
	}
	public List getMailTo()
	{
		return mailTo;
	}
	public void setMailTo(List mailTo)
	{
		this.mailTo = mailTo;
		setChanged();
	}
	
	public String getSmtpHost()
	{
		return smtpHost;
	}
	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
		setChanged();
	}
}
