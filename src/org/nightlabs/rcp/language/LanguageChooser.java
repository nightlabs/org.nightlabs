/*
 * Created on Jan 6, 2005
 */
package org.nightlabs.rcp.language;

import org.nightlabs.language.LanguageCf;

/**
 * This interface must be implemented in all GUI elements that allow
 * choosing a current language. The LanguageChooser is usually only responsible
 * for a local scope - e.g. a view.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * 
 * @see org.nightlabs.ipanema.language.LanguageChooserCombo
 * @see org.nightlabs.ipanema.language.LanguageChooserList
 */
public interface LanguageChooser
{
	/**
	 * @return Returns the currently selected language.
	 */
	LanguageCf getLanguage();
	
	/**
	 * Adds a new LanguageChangeListener to react whenever the language is changed.
	 * @param l The listener to add.
	 */
	void addLanguageChangeListener(LanguageChangeListener l);
	
	/**
	 * Remove a previously added listener. If it is not registered, this method does nothing.
	 * @param l The listener to remove.
	 */
	void removeLanguageChangeListener(LanguageChangeListener l);
	
	/**
	 * This method exists to manually fire a LanguageChangeEvent. This is useful
	 * to initialize a GUI (e.g. view) after all elements have been created.
	 */
	void fireLanguageChangeEvent();
}
