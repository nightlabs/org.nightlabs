/*
 * Created on Jan 6, 2005
 */
package org.nightlabs.base.language;

import java.io.Serializable;

import org.nightlabs.language.LanguageCf;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class LanguageChangeEvent implements Serializable
{
	private LanguageChooser languageChooser;
	private LanguageCf oldLanguage;
	private LanguageCf newLanguage;

	public LanguageChangeEvent(LanguageChooser languageChooser, LanguageCf oldLanguage, LanguageCf newLanguage)
	{
		this.languageChooser = languageChooser;
		this.oldLanguage = oldLanguage;
		this.newLanguage = newLanguage;
	}
	/**
	 * @return Returns the languageChooser.
	 */
	public LanguageChooser getLanguageChooser()
	{
		return languageChooser;
	}
	/**
	 * @return Returns the newLanguage.
	 */
	public LanguageCf getNewLanguage()
	{
		return newLanguage;
	}
	/**
	 * @return Returns the oldLanguage.
	 */
	public LanguageCf getOldLanguage()
	{
		return oldLanguage;
	}
}
