/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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

package org.nightlabs.base.entitylist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import org.nightlabs.base.part.ControllablePart;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class AbstractEntityListView extends ViewPart  implements EntityManagementViewStateChangedListener, ControllablePart
{
  
	private Map entityLists;
	private String activeManagementViewID;

	/**
	 * Should return the ID of the extending view.
	 * Will be used to find EntityList and EntityManagerView registrations.
	 * 
	 * @return This views ID.
	 */
	public abstract String getViewID();
  
  private class EntityManagementViewListener implements IPartListener2 
	{
  	private AbstractEntityListView elv;
  	public EntityManagementViewListener(AbstractEntityListView elv)
  	{
  		this.elv = elv;
  	}
  	
  	public void partActivated(IWorkbenchPartReference partRef) 
  	{
  		Object o = partRef.getPart(false);
  		if(o instanceof EntityManagementView)
  			elv.viewActivated(partRef.getId());
  	}

  	public void partBroughtToTop(IWorkbenchPartReference partRef) 
  	{
//  		System.out.println(partRef.getId() + " : brought to top");
  	}

  	public void partClosed(IWorkbenchPartReference partRef) 
  	{
			if (partRef.getId().equals(elv.activeManagementViewID))
				elv.activeManagementViewID = null; 
  	}

  	public void partDeactivated(IWorkbenchPartReference partRef) 
  	{
  	}

  	public void partOpened(IWorkbenchPartReference partRef) 
  	{
//  		System.out.println(partRef.getId() + " : opened");
  	}

  	public void partHidden(IWorkbenchPartReference partRef) 
  	{
  	}

  	public void partVisible(IWorkbenchPartReference partRef) 
  	{
//  		System.out.println(partRef.getId() + " : visible");
  	}

  	public void partInputChanged(IWorkbenchPartReference partRef) 
  	{
  	}

  }
	private EntityManagementViewListener emvListener;

  
  public AbstractEntityListView()
  {
  	super();
  	entityLists = new HashMap();
  }

  public abstract void createEntityListControl(Composite parent);
	
	public void createPartControl(Composite parent) {
		createEntityListControl(parent);
		
		emvListener = new EntityManagementViewListener(this);
    getViewSite().getPage().addPartListener(emvListener);
    
		Collection registeredEntityLists = EntityListRegistry.sharedInstance().getEntityLists(getViewID());
		if (registeredEntityLists != null) {
			int i = 0;
			String selectEntityID = null;
			for (Iterator iter = registeredEntityLists.iterator(); iter.hasNext();) {
				EntityList el = (EntityList) iter.next();
				addEntityList(el);
				if (i == 0)
					selectEntityID = el.getID();
				++i;
			}
			setSelectedEntityList(selectEntityID);
			showManagerViews(selectEntityID);
		}
  }
  
  public abstract void setFocus();
	
	/** 
	 * Should return a new SelectionChangeListener that
	 * will be notified when this views selection has changed.
	 * Null may be returned also.
	 * 
	 * @return New ISelectionChangedListener or null
	 */
	protected abstract ISelectionChangedListener createListChangeListener();
	
	
	private static Collection EMPTY_COLLECTION = new ArrayList();
	
  public abstract void refresh();

  public abstract String getSelectedListID();
  
  public abstract void setSelectedEntityList(String entityListID);
	
  private void addEntityList(EntityList el)
  {
  	if(!entityLists.containsValue(el))
  	{
  		entityLists.put(el.getID(), el);
  	}
		entityListAdded(el);
  }
	
	protected abstract void entityListAdded(EntityList el);
	
	
	protected void showManagerViews(String entityListID) 
	{
		try 
		{
			List entityManagers = EntityManagerViewRegistry.sharedInstance().getManagerViewsForEntityID(
					getViewID(),
					entityListID
			);
			
			if(entityManagers == null)
				return;
			
			if(activeManagementViewID != null)
			{
				if(entityManagers.contains(activeManagementViewID))
					return;
			}
			
			if (entityManagers != null) 
      {
        Iterator i = entityManagers.iterator();
        if(i.hasNext()) // take the first in the list
        {
          String viewID = (String) i.next();
          getViewSite().getPage().showView(viewID);
          activeManagementViewID = viewID;
        }
//				for (Iterator iter = entityManagers.iterator(); iter.hasNext();) 
//        {
//					String viewID = (String) iter.next();
//					getViewSite().getPage().showView(viewID);
//					activeManagementViewID = viewID;
//				}
			}
		} 
		catch (PartInitException e) 
		{
			throw new RuntimeException(e);
		}
	}

	protected void entityListSelected(String entityListID) {
		showManagerViews(entityListID);
	}
	
	
	public void viewActivated(String viewID) 
	{
		if (!canDisplayPart())
			return;
		List lists = EntityManagerViewRegistry.sharedInstance().getManagedEntitiesForManagerViewID(viewID);
		if(lists == null)
			return;
		Iterator i = lists.iterator();
		String showLid = null;
		while(i.hasNext())
		{
			String lid = (String)i.next();
			if(lid.equals(getSelectedListID()))
			{
				refresh();
				return;
			}
			else
				showLid = lid;
		}
		if(showLid != null)
    {
      activeManagementViewID = viewID;
			setSelectedEntityList(showLid);
      showManagerViews(showLid);
    }
	}

	protected Map getEntityLists() {
		return entityLists;
	}
	
	protected int getEntityListCount() {
		return entityLists.size();
	}
	
	public void dispose()
	{
		super.dispose();
		getViewSite().getPage().removePartListener(emvListener);
	}

}
