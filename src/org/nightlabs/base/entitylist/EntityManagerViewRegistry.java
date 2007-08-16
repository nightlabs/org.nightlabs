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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * Holds extensions to the entityManagerView extension point.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @deprecated I think all the EntityList stuff is deprecated. Right? Marco.
 */
public class EntityManagerViewRegistry extends AbstractEPProcessor {
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.entitymanagerview"; //$NON-NLS-1$

	/**
	 * key: String listViewID<br/>
	 * value: Map<br/>
	 *   key: String managerViewID<br/>
	 *   value: Set of String entityID<br/> 
	 */
	private Map managedEntitiesByListViews = new HashMap();
	
	/**
	 * key: String managerViewID<br/>
	 * value: Set of String listViewID<br/> 
	 */
	private Map listViewsByManager = new HashMap();

	/**
	 * key: String listViewID<br/>
	 * value: Map<br/>
	 *   key: String entityID<br/>
	 *   value: List of String managerViewID<br/> 
	 */
	private Map managerViewsByWrappedEntities = new HashMap();

	/**
	 * key: String enityID<br/>
	 * value: Set of String managerViewID<br/> 
	 */
	private Map managerViewsByEntities = new HashMap();
	
	/**
	 * key: String managerViewID<br/>
	 * value: Set of String entityID<br/> 
	 */
	private Map managedEnitiesByManagerView = new HashMap();
	
	public EntityManagerViewRegistry() {
		super();
	}

	/**
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	
	/**
	 * All viewIDs of Views managing the given entityID in a Set or null
	 * if no managers are registered to this entityID.
	 * 
	 * @param entityID The entityID managers are searched for
	 * @return All viewIDs of Views managing the given entityID in a Set or null
	 */
	public Set getManagerViewsForEntityID(String entityID) {
		if (!isProcessed())
			process();
		return (Set)managerViewsByEntities.get(entityID);
	}

	/**
	 * All viewIDs of Views managing the given entityID whithin the
	 * given listViewID in a List or null
	 * if no managers are registered to this entityID.
	 * 
	 * @param entityID The entityID managers are searched for
	 * @return All viewIDs of Views managing the given entityID in a List or null
	 */
	public List getManagerViewsForEntityID(String listViewID, String entityID) {
		if (!isProcessed())
			process();
		Map managerViewsMap =  (Map)managerViewsByWrappedEntities.get(listViewID);
		if (managerViewsMap == null)
			return null;
		
		return (List)managerViewsMap.get(entityID);
	}

	/**
	 * Retrurns all entityIDs managed by the View with the given managerViewID in a Set or null
	 * if no managed entities are registered to this viewID.
	 * This will include all registrations no matter to what listViewID they point.
	 * 
	 * @param managerViewID The viewID managed entities are searched for
	 * @return All entityIDs managed by the View with the given viewID in a Set or null
	 */
	public List getManagedEntitiesForManagerViewID(String managerViewID) {
		checkProcessing();
		return (List)managedEnitiesByManagerView.get(managerViewID);
	}

	/**
	 * Returns all listViewID the given View with the managerViewID
	 * was registered to.
	 * 
	 * @param managerViewID
	 */
	public Set getListViewsForManagerView(String managerViewID) {
		checkProcessing();
		return (Set)listViewsByManager.get(managerViewID);
	}
	/**
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception {
		if (element.getName().equalsIgnoreCase("entityManagerView")) { //$NON-NLS-1$
			
			String entityID = element.getAttribute("entityID"); //$NON-NLS-1$
			if (entityID == null || "".equals(entityID)) //$NON-NLS-1$
				throw new EPProcessorException("Element entityManagerView has to define an attribute entityID"); //$NON-NLS-1$
				
			String listViewID = element.getAttribute("listViewID"); //$NON-NLS-1$
			if (listViewID == null || "".equals(listViewID)) //$NON-NLS-1$
				throw new EPProcessorException("Element entityManagerView has to define an attribute listViewID"); //$NON-NLS-1$

			String managerViewID = element.getAttribute("managerViewID"); //$NON-NLS-1$
			if (managerViewID == null || "".equals(managerViewID)) //$NON-NLS-1$
				throw new EPProcessorException("Element entityManagerView has to define an attribute managerViewID"); //$NON-NLS-1$

			
			/*
			 * key: String listViewID<br/>
			 * value: Map<br/>
			 *   key: String managerViewID<br/>
			 *   value: Set of String entityID<br/> 
			 */
			{
				Map managerViewEntities = (Map)managedEntitiesByListViews.get(listViewID);
				if (managerViewEntities == null) {
					managerViewEntities = new HashMap();
					managedEntitiesByListViews.put(listViewID, managerViewEntities);
				}
				Set entities = (Set)managerViewEntities.get(managerViewID);
				if (entities == null) {
					entities = new HashSet();
					managerViewEntities.put(managerViewID, entities);					
				}
				entities.add(entityID);
			}
			
			/*
			 * key: String managerViewID<br/>
			 * value: Set of String listViewID<br/> 
			 */
			{
				Set listViews = (Set)listViewsByManager.get(managerViewID);
				if (listViews == null) {
					listViews = new HashSet();
					listViewsByManager.put(managerViewID, listViews);
				}
				listViews.add(listViewID);
			}

			/*
			 * key: String listViewID<br/>
			 * value: Map<br/>
			 *   key: String entityID<br/>
			 *   value: Set of String managerViewID<br/> 
			 */
			{
				Map managerMap = (Map)managerViewsByWrappedEntities.get(listViewID);
				if (managerMap == null) {
					managerMap = new HashMap();
					managerViewsByWrappedEntities.put(listViewID, managerMap);
				}
				List managers = (List)managerMap.get(entityID);
				if (managers == null) {
					managers = new ArrayList();
					managerMap.put(entityID, managers);
				}
				managers.add(managerViewID);
			}

			/*
			 * key: String enityID<br/>
			 * value: Set of String managerViewID<br/> 
			 */
			{
				Set managers = (Set)managerViewsByEntities.get(entityID);
				if (managers == null) {
					managers = new HashSet();
					managerViewsByEntities.put(entityID, managers);					
				}
				managers.add(managerViewID);
			}
			
			/*
			 * key: String managerViewID<br/>
			 * value: Set of String entityID<br/> 
			 */
			{
				List entities = (List)managedEnitiesByManagerView.get(managerViewID);
				if (entities == null) {
					entities = new ArrayList();
					managedEnitiesByManagerView.put(managerViewID, entities);					
				}
				entities.add(entityID);
			}
			
		}
	}
	
	
	private static EntityManagerViewRegistry sharedInstance;
	
	public static EntityManagerViewRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new EntityManagerViewRegistry();
		return sharedInstance;
	}

}
