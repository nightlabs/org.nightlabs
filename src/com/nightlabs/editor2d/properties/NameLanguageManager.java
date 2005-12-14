/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 03.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.nightlabs.language.LanguageCf;
import com.nightlabs.rcp.language.ILanguageManager;
import com.nightlabs.rcp.language.LanguageChangeEvent;
import com.nightlabs.rcp.language.LanguageChangeListener;
import com.nightlabs.rcp.language.LanguageChooser;
import com.nightlabs.rcp.language.LanguageManager;

public class NameLanguageManager
implements ILanguageManager,
					 LanguageChooser
{
	protected static NameLanguageManager langMan = null;
	public static NameLanguageManager sharedInstance() {
		if (langMan == null)
			langMan = new NameLanguageManager();
		return langMan;
	}
	
	public NameLanguageManager() {
		super();
		init();
		addLanguage(Locale.ENGLISH.getLanguage());
		addLanguage(Locale.FRENCH.getLanguage());
	}

	protected void init() 
	{
		LanguageManager languageMan = LanguageManager.sharedInstance();
		for (Iterator it = languageMan.getLanguages().iterator(); it.hasNext(); ) {
			LanguageCf langCf = (LanguageCf) it.next();
			addLanguage(langCf.getLanguageID());
		}
	}
	
//	protected String currentLanguageID = LanguageManager.sharedInstance().getCurrentLanguageID();
//	public void setCurrentLanguageID(String languageID) {
//		currentLanguageID = languageID;
//	}	
//	public String getCurrentLanguageID() {
//		return currentLanguageID;
//	}
	public void setCurrentLanguageID(String languageID) 
	{
		for (Iterator it = getLanguages().iterator(); it.hasNext(); ) {
			LanguageCf langCf = (LanguageCf) it.next();
			if (languageID.equals(langCf.getLanguageID())) {
				currentLanguage = langCf;
				fireLanguageChangeEvent();
			}
		}
	}
	public String getCurrentLanguageID() {
		return currentLanguage.getLanguageID();
	}
	protected LanguageCf currentLanguage = LanguageManager.sharedInstance().getCurrentLanguage();
	public LanguageCf getLanguage() {
		return currentLanguage;
	}
	
	
	protected Collection languages = new ArrayList();
	public void addLanguage(String languageID) {
		LanguageCf langCf = LanguageManager.createLanguage(languageID);
		languages.add(langCf);
	}
	public Collection getLanguages() {
//		return LanguageManager.sharedInstance().getLanguages();
		return languages;		
	}
		
	protected List languageChangeListeners = new LinkedList();
	protected LanguageCf oldLanguage = null;	
	
	public void addLanguageChangeListener(LanguageChangeListener l) {
		languageChangeListeners.add(l);
	}

	public void removeLanguageChangeListener(LanguageChangeListener l) {
		languageChangeListeners.remove(l);
	}

	public void fireLanguageChangeEvent() 
	{
		if (languageChangeListeners.size() < 1)
			return;

		LanguageChangeEvent languageChangeEvent = new LanguageChangeEvent(this, oldLanguage, getLanguage());
		for (Iterator it = languageChangeListeners.iterator(); it.hasNext(); ) {
			((LanguageChangeListener)it.next()).languageChanged(languageChangeEvent);
		}

		oldLanguage = getLanguage();		
	}	
}
