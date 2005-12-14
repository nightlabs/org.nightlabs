/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 03.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.language;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nightlabs.language.LanguageCf;

public abstract class AbstractSimpleLanguageChooser 
implements LanguageChooser
{
	protected List languageChangeListeners = new LinkedList();
	protected LanguageCf oldLanguage = null;
	
	public AbstractSimpleLanguageChooser() {
		super();
	}

	public LanguageCf getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

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
