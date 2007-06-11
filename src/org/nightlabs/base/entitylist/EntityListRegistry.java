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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * Holds extensions to the entityList extension point.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class EntityListRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.entitylist";
	
	private static class ListCarrier implements Comparable {
		private int priority;
		private EntityList entityList;
		public ListCarrier(int priority, EntityList entityList) {
			this.priority = priority;
			this.entityList = entityList;
		}
		
		public int compareTo(Object object) {
			if (object instanceof ListCarrier) {
				ListCarrier other = (ListCarrier)object;
				if (other.priority < this.priority)
					return 1;
				else if (other.priority > this.priority)
					return -1;
				else
					return 0;
			}
			return 0;
		}
	}
	
	/**
	 * key: String viewID;<br/>
	 * value: Map entityLists<br/>
	 *   key: String entityListID<br/>
	 *   value: ListCarrier<br/> 
	 */
	private Map entityListsByViews = new HashMap();
	
	public EntityListRegistry() {
		super();
	}

	/**
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	
	/**
	 * Will return List of all EntityLists registered to this viewID ordered
	 * according to the priority defined on registration. If no EntityList is
	 * registered to the given viewID null will be returned.
	 * 
	 * @param viewID
	 * @return Collection of EntityLists or null
	 */
	public List getEntityLists(String viewID) {
		if (!isProcessed())
			process();
		Map listMap = (Map)entityListsByViews.get(viewID);
		if (listMap == null)
			return null;
		List sortedCarriers = new ArrayList();
		sortedCarriers.addAll(listMap.values());
		Collections.sort(sortedCarriers);
		List result = new ArrayList();
		for (Iterator iter = sortedCarriers.iterator(); iter.hasNext();) {
			ListCarrier carrier = (ListCarrier) iter.next();
			result.add(carrier.entityList);
		}
		return result;
	}
	

	/**
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception {
		if (element.getName().equalsIgnoreCase("entityList")) {
			String id = element.getAttribute("id");
			if (id == null || "".equals(id))
				throw new EPProcessorException("Element entityList must define an attribute id.");
			String viewID = element.getAttribute("viewID");
			if (viewID == null || "".equals(viewID))
				throw new EPProcessorException("Element entityList must define an attribute viewID.");
			
			EntityList entityList = null; 
			try {
				entityList = (EntityList)element.createExecutableExtension("class");
			} catch (CoreException e) {
				throw new EPProcessorException(e);
			}
			
			String priorityStr = element.getAttribute("priority");
			int priority = 500;
			if (priorityStr != null || !"".equals(priorityStr))
				try {
					priority = Integer.parseInt(priorityStr);
				} catch (NumberFormatException e) {
					priority = 500;
				}
			
			ListCarrier carrier = new ListCarrier(priority, entityList);
				
				
			Map listMap = (Map)entityListsByViews.get(viewID);
			if (listMap == null) {
				listMap = new HashMap();
				entityListsByViews.put(viewID, listMap);
			}
			listMap.put(id, carrier);
		}
	}
	
	private static EntityListRegistry sharedInstance;
	
	public static EntityListRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new EntityListRegistry();
		return sharedInstance;
	}

}
