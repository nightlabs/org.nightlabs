/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 03.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.language;

import java.util.Collection;

public interface ILanguageManager
{
	/** 
	 * @return the current languageID
	 */	
	public String getCurrentLanguageID();
	
	/**
	 * adds a Language based on the given languageID
	 * @param languageID the ID of the Language
	 */
	public void addLanguage(String languageID);
	
	/** 
	 * @return a java.util.Collection which contains com.nightlabs.language.LanguageCf ´s
	 */
	public Collection getLanguages();
	
	/** 
	 * @param newLanguageID the id of the new Language
	 */
	public void setCurrentLanguageID(String newLanguageID); 
}
