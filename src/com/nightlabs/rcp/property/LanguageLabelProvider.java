/**
 * <p> Project: com.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.property;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.i18n.I18nText;
import com.nightlabs.rcp.language.LanguageManager;

public class LanguageLabelProvider 
extends LabelProvider
{
	protected I18nText text;
	public LanguageLabelProvider(I18nText text) {
		super();
		this.text = text;
	}
	
  public String getText(Object element) 
  {
    if (element == null)
        return ""; //$NON-NLS-1$

    if (element instanceof I18nText) {
    	I18nText i18nText = (I18nText) element;
    	if (i18nText != null)
    		return i18nText.getText(LanguageManager.sharedInstance().getCurrentLanguageID());
    }

    return ""; //$NON-NLS-1$
  }
  
  public Image getImage(Object element) 
  {
    if (element == null)
      return null; //$NON-NLS-1$

    return LanguageManager.getImage(LanguageManager.sharedInstance().getCurrentLanguageID());    
 	}
  
//  protected String getLanguageID(String languageText) 
//  {
//  	for (Iterator it = text.getTexts().iterator(); it.hasNext(); ) {
//  		Map.Entry entry = (Map.Entry) it.next();
//  		String value = (String) entry.getValue();
//  		if (value.equals(languageText)) {
//  			String languageID = (String) entry.getKey();
//  			return languageID;
//  		}
//  	}
//  	return null;
//  }
}
