/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 03.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.action;

import org.eclipse.jface.action.Action;

import com.nightlabs.rcp.language.ILanguageManager;
import com.nightlabs.rcp.language.LanguageManager;
import com.nightlabs.rcp.ressource.SharedImages;

public class LanguageAction 
extends Action
{
	protected static final String ID = LanguageAction.class.getName();
	
	protected ILanguageManager langMan;
	protected String languageID;
	public LanguageAction(ILanguageManager langMan, String languageID) {
		super();
		this.langMan = langMan;
		this.languageID = languageID;
		init();
	}

	protected void init() 
	{
		setId(ID+languageID);
		setText(LanguageManager.getNativeLanguageName(languageID));
		setImageDescriptor(SharedImages.getImageDescriptor(languageID));
	}

	public void run() 
	{
		langMan.setCurrentLanguageID(languageID);
	}
	
	
}
