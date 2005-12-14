/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 20.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.action.selection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import com.nightlabs.rcp.action.registry.AbstractActionRegistry;
import com.nightlabs.rcp.extensionpoint.EPProcessorException;

public class SelectionActionRegistry 
extends AbstractActionRegistry
{
	private static SelectionActionRegistry _sharedInstance;

	private static boolean initializingSharedInstance = false;
	public static synchronized SelectionActionRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!");

		if (_sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				_sharedInstance = new SelectionActionRegistry();
				_sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return _sharedInstance;
	}	
	
	public static final String EXTENSION_POINT_ID = "com.nightlabs.base.selectionActionRegistry";	
	private static final String ATTRIBUTE_NAME_ACTION_CLASS = "class";	
	
	public SelectionActionRegistry() {
		super();
	}

	protected Object createActionOrContributionItem(IExtension extension, IConfigurationElement element) 
	throws EPProcessorException 
	{
		String className = element.getAttribute(ATTRIBUTE_NAME_ACTION_CLASS);
		if (className == null || "".equals(className))
			return null;

		SelectionAction res;
		try {
			res = (SelectionAction) element.createExecutableExtension(ATTRIBUTE_NAME_ACTION_CLASS);
		} catch (CoreException e) {
			throw new EPProcessorException(e);
		}
		return res;
	}

	public static final String SELECTION_ZONE = "Selection Zone";
	
//	protected void initAction(IAction _action, IExtension extension, IConfigurationElement element) throws EPProcessorException
//	{
//		SelectionAction action = (SelectionAction) _action;
//		action.init(SELECTION_ZONE, Object.class, "SelectionAction");
//	}	
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	protected String getActionElementName()
	{
		return "selectionAction";
	}	
}
