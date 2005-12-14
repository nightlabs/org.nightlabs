/*
 * Created 	on Jun 1, 2005
 * 					by alex
 *
 */
package com.nightlabs.base.entitylist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import com.nightlabs.rcp.extensionpoint.AbstractEPProcessor;
import com.nightlabs.rcp.extensionpoint.EPProcessorException;

/**
 * Holds extensions to the entityList extension point.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class EntityListRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "com.nightlabs.base.entitylist";
	
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
	 * @see com.nightlabs.rcp.extensionpoint.AbstractEPProcessor#getExtensionPointID()
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
			try {
				process();
			} catch (EPProcessorException e) {
				throw new RuntimeException(e);
			}
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
	 * @see com.nightlabs.rcp.extensionpoint.AbstractEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public void processElement(IExtension extension, IConfigurationElement element)
			throws EPProcessorException {
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
