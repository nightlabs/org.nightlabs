/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 03.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.nightlabs.base.language.ILanguageManager;
import org.nightlabs.base.language.LanguageChangeEvent;
import org.nightlabs.base.language.LanguageChangeListener;
import org.nightlabs.base.language.LanguageChooser;
import org.nightlabs.base.language.LanguageManager;
import org.nightlabs.language.LanguageCf;

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
