/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.nightlabs.base.language.ILanguageManager;
import org.nightlabs.base.language.LanguageAction;
import org.nightlabs.base.language.LanguageChangeEvent;
import org.nightlabs.base.language.LanguageChangeListener;
import org.nightlabs.base.language.LanguageContributionItem;
import org.nightlabs.base.language.LanguageManager;
import org.nightlabs.language.LanguageCf;

public class EditorPropertyPage 
extends PropertySheetPage
{	
	public EditorPropertyPage() 
	{
		super();
		langMan = LanguageManager.sharedInstance();
		langMan.addPropertyChangeListener(languageListener);
	}

	protected PropertyChangeListener languageListener = new PropertyChangeListener()
	{	
		public void propertyChange(PropertyChangeEvent evt) 
		{
			if (!getControl().isDisposed()) {
				refresh();
			}			
		}	
	}; 
	
//	protected LanguageChangeListener langListener = new LanguageChangeListener()
//	{	
//		public void languageChanged(LanguageChangeEvent event) 
//		{			
//			if (!getControl().isDisposed()) {
//				refresh();
//			}
//		}	
//	};
		
	protected LanguageManager langMan = null;
	
	protected LanguageContributionItem langContribution = null;
  public void makeContributions(IMenuManager menuManager,
      IToolBarManager toolBarManager, IStatusLineManager statusLineManager) 
  {
//  	langContribution = new LanguageContributionItem(langMan);
//  	toolBarManager.add(langContribution);
  	
  	super.makeContributions(menuManager, toolBarManager, statusLineManager);  	
  	
//  	Collection languageActions = makeLanguageActions();
//  	for (Iterator it = languageActions.iterator(); it.hasNext(); ) {
//  		LanguageAction action = (LanguageAction) it.next();
//  		menuManager.add(action);
//  	}  	
  }
  
//  protected Collection makeLanguageActions() 
//  {
//  	Collection languageActions = new ArrayList(langMan.getLanguages().size());
//  	for (Iterator it = langMan.getLanguages().iterator(); it.hasNext(); ) {
//  		LanguageCf language = (LanguageCf) it.next();
//  		LanguageAction action = new LanguageAction(langMan, language.getLanguageID());
//  		languageActions.add(action);  		
//  	}
//  	return languageActions;
//  }
  
}
