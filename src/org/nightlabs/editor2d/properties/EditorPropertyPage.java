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

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.nightlabs.base.language.LanguageContributionItem;

public class EditorPropertyPage 
extends PropertySheetPage
{	
	public static final Logger LOGGER = Logger.getLogger(EditorPropertyPage.class);
	
	public EditorPropertyPage() 
	{
		super();		
		unitManager = new UnitManager();		
	}

	private UnitManager unitManager = null;
	public UnitManager getUnitManager() {
		return unitManager;
	}
	public void setUnitManager(UnitManager unitManager) {
		this.unitManager = unitManager;
	}
	
  public void makeContributions(IMenuManager menuManager,
      IToolBarManager toolBarManager, IStatusLineManager statusLineManager) 
  {
//  	LanguageContributionItem langContribution = new LanguageContributionItem();
//  	toolBarManager.add(langContribution);
  	
  	UnitContributionItem unitContributionItem = new UnitContributionItem(getUnitManager());
  	unitContributionItem.selectUnit(getUnitManager().getCurrentUnit());
  	toolBarManager.add(unitContributionItem);
  	
  	super.makeContributions(menuManager, toolBarManager, statusLineManager);  	  	
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
  
//	protected LanguageChangeListener langListener = new LanguageChangeListener()
//	{	
//		public void languageChanged(LanguageChangeEvent event) 
//		{			
//			if (!getControl().isDisposed()) {
//				refresh();
//			}
//		}	
//	};
  
//	private PropertyChangeListener languageListener = new PropertyChangeListener()
//	{	
//		public void propertyChange(PropertyChangeEvent evt) 
//		{
//			if (!getControl().isDisposed()) {
//				refresh();
//			}			
//		}	
//	};
	
//	@Override
//	public void createControl(Composite parent) 
//	{
//		super.createControl(parent);
//		
//		IMenuManager menuMan = getSite().getActionBars().getMenuManager();
//		makeUnitActions(menuMan);		
//	}
//	
//	protected void makeUnitActions(IMenuManager menuMan) 
//	{
//		for (IUnit unit : getUnitManager().getUnits()) {
//			UnitAction action = new UnitAction(getUnitManager(), unit);
//			menuMan.add(action);
//		}
//	}
//	
//	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
//	{
//		super.selectionChanged(part, selection);
//	  if (selection instanceof IStructuredSelection) 
//	  {
//	  	Object[] sel = ((IStructuredSelection) selection).toArray();
//	  	for (int i=0; i<sel.length; i++) 
//	  	{
//	  		Object o = sel[i];
//	  		if (o instanceof AbstractDrawComponentEditPart) {
//	  			AbstractDrawComponentEditPart ep = (AbstractDrawComponentEditPart) o;
//	  			IPropertySource ps = ep.getPropertySource();
//	  			if (ps instanceof DrawComponentPropertySource) {
//	    			DrawComponentPropertySource dcps = (DrawComponentPropertySource) ps;
//	    			IUnit currentUnit = getUnitManager().getCurrentUnit();
//	    			dcps.setUnit(currentUnit);
//	    			LOGGER.debug("set currentUnit "+currentUnit+" for DrawComponentPropertySource "+dcps+" of EditPart "+ep);    				
//	  			}
//	  		}
//	  	}
//	  }  	
//	}	
	
}
