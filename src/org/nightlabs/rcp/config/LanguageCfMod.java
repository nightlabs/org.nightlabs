/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 01.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.config;

import org.nightlabs.config.CfModList;
import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;
import org.nightlabs.rcp.language.LanguageManager;

public class LanguageCfMod 
extends ConfigModule
{
	public LanguageCfMod() {
		super();
	}

	protected CfModList languages = null;	
	public CfModList getLanguages() {
		return languages;
	}
	public void setLanguages(CfModList languages) {
		this.languages = languages;		
	}

	public void init() 
	throws InitException 
	{
		super.init();
		if (languages == null || languages.isEmpty())
			languages = createDefaultLanguage();
		languages.setOwnerCfMod(this);
	}

	protected CfModList createDefaultLanguage() 
	{
		CfModList l = new CfModList(this);
		l.add(LanguageManager.createDefaultLanguage());		
		return l;
	}
	
//	public static Collection createDefaultLanguages() 
//	{
//		List languages = new ArrayList();
//		languages.add(Locale.ENGLISH.getLanguage());
//		languages.add(Locale.GERMAN.getLanguage());
//		languages.add(Locale.FRENCH.getLanguage());
//		return languages;
//	}
}
