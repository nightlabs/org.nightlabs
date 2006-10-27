/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/
package org.nightlabs.base.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.entity.editor.EntityEditorPageSettings;
import org.nightlabs.base.entity.tree.IEntityTreeCategory;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * An extension point registry for entity tree categories
 * and editor pages.
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class EntityEditorRegistry extends AbstractEPProcessor
{
	private static final Logger logger = Logger.getLogger(EntityEditorRegistry.class);

	/**
	 * The extension point id this registry is for.
	 */
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.entityEditor";

	/**
	 * The shared instance
	 */
	private static EntityEditorRegistry sharedInstance = null;

	/**
	 * Get the lazily created shared instance.
	 * @return The shared instance
	 */
	public static EntityEditorRegistry sharedInstance()
	{
		if (sharedInstance == null)
			sharedInstance = new EntityEditorRegistry();
		return sharedInstance;
	}

	/**
	 * Category extensions.
	 */
	private Map<String, List<IEntityTreeCategory>> categoriesByViewID;

	/**
	 * Editor page extensions.
	 */
	private Map<String, Set<EntityEditorPageSettings>> pageSettings;
	
	
	public void addPage(String editorID, EntityEditorPageSettings settings)
	{
		if(pageSettings == null)
			pageSettings = new HashMap<String, Set<EntityEditorPageSettings>>();
		Set<EntityEditorPageSettings> editorPages = pageSettings.get(editorID);
		if(editorPages == null) {
			editorPages = new HashSet<EntityEditorPageSettings>();
			pageSettings.put(editorID, editorPages);
		}
		editorPages.add(settings);
	}
	
	public Set<EntityEditorPageSettings> getPageSettings(String editorID)
	{
		checkProcessing();
		return pageSettings==null ? null : pageSettings.get(editorID);
	}
	
	public List<EntityEditorPageSettings> getPageSettingsOrdered(String editorID)
	{
		checkProcessing();
		Set<EntityEditorPageSettings> pagesUnordered = getPageSettings(editorID);
		if(pagesUnordered == null)
			return new ArrayList<EntityEditorPageSettings>(0);
		List<EntityEditorPageSettings> pagesOrdered = new ArrayList<EntityEditorPageSettings>(pagesUnordered);
		Collections.sort(pagesOrdered, new Comparator<EntityEditorPageSettings>() {
			public int compare(EntityEditorPageSettings o1, EntityEditorPageSettings o2)
			{
				return o1.getIndexHint() - o2.getIndexHint();
			}
		});
		return pagesOrdered;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID()
	{
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws EPProcessorException
	{
		try {
			if("category".equals(element.getName())) {
				if(categoriesByViewID == null)
					categoriesByViewID = new HashMap<String, List<IEntityTreeCategory>>();

				IEntityTreeCategory category = (IEntityTreeCategory)element.createExecutableExtension("class");

				Set<String> viewIDs = new HashSet<String>();
				IConfigurationElement[] children = element.getChildren();System.out.println(extension.getNamespaceIdentifier());
				for (IConfigurationElement child : children) {
					if ("viewBinding".equals(child.getName())) {
						String viewID = child.getAttribute("viewID");
						if (!checkString(viewID))
							throw new EPProcessorException("The viewID attribute has to be defined.", extension);
						if (viewIDs.contains(viewID))
							logger.warn("The viewID \""+viewID+"\" is defined twice within the EntityTreeCategory \""+category.getId()+"\"! Plugin: " + extension.getNamespaceIdentifier());
						else
							viewIDs.add(viewID);
					}
				}

				for (String viewID : viewIDs) {
					List<IEntityTreeCategory> categories = categoriesByViewID.get(viewID);
					if (categories == null) {
						categories = new ArrayList<IEntityTreeCategory>();
						categoriesByViewID.put(viewID, categories);
					}
					categories.add(category);
				}
			}
			else if("pageFactory".equals(element.getName())) {
				String editorID = element.getAttribute("editorID");
				addPage(editorID, new EntityEditorPageSettings(extension, element));
			}
		} catch (CoreException e) {
			throw new EPProcessorException("processElement failed", extension, e);
		}
	}

	/**
	 * Overrides to sort the categories 
	 * after processing is done.
	 */
	@Override
	public synchronized void process() 
	throws EPProcessorException 
	{
		super.process();
		for (List<IEntityTreeCategory> categories : categoriesByViewID.values()) {
			Collections.sort(categories);
		}
	}

	/**
	 * Get the category extensions for the given viewID.
	 * 
	 * @param viewID The id of the view registered categories should be searched for. 
	 * @return The category extensions for the given viewID.
	 */
	public IEntityTreeCategory[] getCategories(String viewID)
	{
		checkProcessing();
		List<IEntityTreeCategory> list = categoriesByViewID.get(viewID);
		IEntityTreeCategory[] emptyArray = new IEntityTreeCategory[0];
		if (list != null)
			return list.toArray(emptyArray);
		else
			return emptyArray;
	}
}
