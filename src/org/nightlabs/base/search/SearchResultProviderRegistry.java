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
package org.nightlabs.base.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SearchResultProviderRegistry 
extends AbstractEPProcessor 
{
	private static final Logger logger = Logger.getLogger(SearchResultProviderRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.searchResultProvider";
	public static final String ELEMENT_SEARCH_RESULT_PROVIDER = "searchResultProvider";
	public static final String ATTRIBUTE_FACTORY_CLASS = "factoryClass";
	public static final String ATTRIBUTE_DEFAULT = "default";
	public static final String ATTRIBUTE_DECORATOR = "decorator";

	private static SearchResultProviderRegistry sharedInstance;
	public static SearchResultProviderRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (SearchResultProviderRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new SearchResultProviderRegistry();
				}
			}
		}
		return sharedInstance;
	}
	
	protected SearchResultProviderRegistry() {}
	
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws EPProcessorException 
	{
		if (element.getName().equals(ELEMENT_SEARCH_RESULT_PROVIDER)) 
		{
			if (checkString(element.getAttribute(ATTRIBUTE_FACTORY_CLASS))) {
				try {
					SearchResultProviderFactory factory = (SearchResultProviderFactory) element.createExecutableExtension(ATTRIBUTE_FACTORY_CLASS);
					ISearchResultProvider searchResultProvider = factory.createSearchResultProvider();
					String context = searchResultProvider.getContext();
					String name = searchResultProvider.getName().getText();
					name2SearchResultProvider.put(name, factory);
					context2SearchResultProvider.put(context, factory);
					
					String defaultValue = element.getAttribute(ATTRIBUTE_DEFAULT);
					if (defaultValue != null && defaultValue.equalsIgnoreCase(Boolean.toString(true))) {
						defaultName = name; 
					}
					
					String decoratorString = element.getAttribute(ATTRIBUTE_DECORATOR);
					if (checkString(decoratorString)) {
						ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(extension.getNamespaceIdentifier(), decoratorString);
						if (imageDescriptor != null) {
							name2Image.put(name, imageDescriptor.createImage());
						}
					}
				} catch (Exception e) {
					logger.error("There occured an error during initalizing the class "+element.getAttribute(ATTRIBUTE_FACTORY_CLASS), e);
				}				
			}
		}
	}

	private Map<String, SearchResultProviderFactory> name2SearchResultProvider = new HashMap<String, SearchResultProviderFactory>();
	private Map<String, SearchResultProviderFactory> context2SearchResultProvider = new HashMap<String, SearchResultProviderFactory>();
	
	public Collection<String> getRegisteredNames() {
		checkProcessing();
		return name2SearchResultProvider.keySet();
	}
	
	public Collection<String> getRegisteredContexts() {
		checkProcessing();
		return context2SearchResultProvider.keySet();
	}
	
	public ISearchResultProvider getSearchResultProvider(String name) {
		checkProcessing();
		SearchResultProviderFactory factory = name2SearchResultProvider.get(name);
		if (factory != null)
			return factory.createSearchResultProvider();
		return null;
	}
	
	private String defaultName = null;
	public String getDefault() 
	{
		checkProcessing();
		if (defaultName != null)
			return defaultName;
		if (defaultName == null && !getRegisteredNames().isEmpty())
			return getRegisteredNames().iterator().next();
		
		return null;
	}
	
	private Map<String, Image> name2Image = new HashMap<String, Image>();
	public Image getImage(String name) 
	{
		Image image = name2Image.get(name);
		if (image != null) {
			SearchCompositeImage searchImage = new SearchCompositeImage(image);
			return searchImage.createImage();
		}
		return null;
	}
	
}
