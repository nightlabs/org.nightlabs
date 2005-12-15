/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.views.properties.PropertySheetPage;

import org.nightlabs.language.LanguageCf;
import org.nightlabs.rcp.action.LanguageAction;
import org.nightlabs.rcp.language.LanguageChangeEvent;
import org.nightlabs.rcp.language.LanguageChangeListener;

public class EditorPropertyPage 
extends PropertySheetPage
{
//	public EditorPropertyPage(ILanguageManager langMan) {
//		this.langMan = langMan;
//	}
	protected NameLanguageManager langMan;	
	public EditorPropertyPage() 
	{
		langMan = NameLanguageManager.sharedInstance();
		langMan.addLanguageChangeListener(langListener);
	}
	
//	protected LanguageChangeListener langListener = new LanguageChangeListener(){	
//		public void languageChanged(LanguageChangeEvent event) 
//		{			
//			refresh();
//		}	
//	};
	protected LanguageChangeListener langListener = new LanguageChangeListener(){	
		public void languageChanged(LanguageChangeEvent event) 
		{			
			if (!getControl().isDisposed()) {
				refresh();
			}
		}	
	};
	
  public void makeContributions(IMenuManager menuManager,
      IToolBarManager toolBarManager, IStatusLineManager statusLineManager) 
  {
//  	super.makeContributions(menuManager, toolBarManager, statusLineManager);
  	Collection languageActions = makeLanguageActions();
  	for (Iterator it = languageActions.iterator(); it.hasNext(); ) {
  		LanguageAction action = (LanguageAction) it.next();
  		menuManager.add(action);
  	}  		
  }
  
  protected Collection makeLanguageActions() 
  {
  	Collection languageActions = new ArrayList(langMan.getLanguages().size());
  	for (Iterator it = langMan.getLanguages().iterator(); it.hasNext(); ) {
  		LanguageCf language = (LanguageCf) it.next();
  		LanguageAction action = new LanguageAction(langMan, language.getLanguageID());
  		languageActions.add(action);  		
  	}
  	return languageActions;
  }
  
}
