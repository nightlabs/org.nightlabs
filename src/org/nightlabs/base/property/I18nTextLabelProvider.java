/**
 * <p> Project: org.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.property;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.nightlabs.base.language.LanguageManager;
import org.nightlabs.i18n.I18nText;

public class I18nTextLabelProvider 
extends LabelProvider
{
	protected I18nText text;
	protected String languageID;
	public I18nTextLabelProvider(I18nText text, String languageID) {
		super();
		this.text = text;
		this.languageID = languageID;
	}
	
  public String getText(Object element) 
  {
    if (element == null)
        return ""; //$NON-NLS-1$

    if (element instanceof I18nText) {
    	I18nText i18nText = (I18nText) element;
    	if (i18nText != null)
    		return i18nText.getText(languageID);
    }

    return super.getText(element);    	
  }
  
  public Image getImage(Object element) 
  {
    return LanguageManager.getImage(languageID);
//    if (element instanceof I18nText) {
//    	I18nText i18nText = (I18nText) element;
//    	if (i18nText != null) {
//    		String languageText = i18nText.getText();
//    		String languageID = getLanguageID(languageText);
////    		ImageDescriptor imageDesc = SharedImages.getImageDescriptor(languageID);
//    		// TODO: this is jsut a test
//    		ImageDescriptor imageDesc = SharedImages.getImageDescriptor(I18nText.DEFAULT_LANGUAGEID);
//    		if (imageDesc != null)
//    			return imageDesc.createImage();
//    	}    		
//    }
//    
//    return null;
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
