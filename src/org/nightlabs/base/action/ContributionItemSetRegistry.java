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
package org.nightlabs.base.action;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.action.registry.ActionDescriptor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ContributionItemSetRegistry 
extends AbstractActionRegistry 
{
	private static final Logger logger = Logger.getLogger(ContributionItemSetRegistry.class);
	
	private static ContributionItemSetRegistry sharedInstance;
	private static boolean initializingSharedInstance = false;
	public static synchronized ContributionItemSetRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!");

		if (sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				sharedInstance = new ContributionItemSetRegistry();
				sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return sharedInstance;
	}
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.contributionItemSet";
	public static final String ELEMENT_CONTRIBUTION_ITEM = "contributionItem";
	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_NAME = "name";
	
	protected ContributionItemSetRegistry() {
		super();
	}

	@Override
	protected Object createActionOrContributionItem(IExtension extension, IConfigurationElement element) 
	throws EPProcessorException 
	{
		if (element.getName().equals(ELEMENT_CONTRIBUTION_ITEM)) 
		{
			String id = element.getAttribute(ATTRIBUTE_ID);
			if (checkString(id))
				logger.error("id is empty!");
			
			String name = element.getAttribute(ATTRIBUTE_NAME);
			if (checkString(name))
				logger.error("name is empty!");
			
			String className = element.getAttribute(ATTRIBUTE_CLASS);			
			if (checkString(className)) {
				try {
					IXContributionItem contributionItem = (IXContributionItem) element.createExecutableExtension(ATTRIBUTE_CLASS);
					contributionItem.setId(id);
					if (contributionItem instanceof AbstractContributionItem) {
						AbstractContributionItem abstractContributionItem = (AbstractContributionItem) contributionItem;
						abstractContributionItem.setName(name);
					}
					return contributionItem;
				} catch (CoreException e) {
					logger.error("Could not instantiate Class "+className+"!", e);
					throw new EPProcessorException("Could not instantiate Class "+className+"!", e);
				}
			}
		}
		return null;
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	protected String getActionElementName() {
		return ELEMENT_CONTRIBUTION_ITEM;
	}

	@Override
	public int contributeToCoolBar(ICoolBarManager coolBarManager) 
	{
//		return super.contributeToCoolBar(coolBarManager);
		checkPerspectiveListenerAdded();
		
		if (!isAffectedOfPerspectiveExtension()) {
			for (ActionDescriptor actionDescriptor : getActionDescriptors()) {
				IXContributionItem contributionItem = actionDescriptor.getContributionItem();
				coolBarManager.add(contributionItem);
			}
			return getActionDescriptors().size();			
		} 
		else {
			if (getActiveExtensionIDs() != null) {
				for (String extensionID : getActiveExtensionIDs()) {
					ActionDescriptor actionDescriptor = getActionDescriptor(extensionID, false);
					IXContributionItem contributionItem = actionDescriptor.getContributionItem();
					coolBarManager.add(contributionItem);
				}
				return getActionDescriptors().size();							
			}
			return 0;
		}		
	}
		
}
