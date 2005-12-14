/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 01.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.action;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.rcp.extensionpoint.AbstractEPProcessor;
import org.nightlabs.rcp.extensionpoint.EPProcessorException;

public class NewFileRegistry 
extends AbstractEPProcessor
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.newfileaction";	
	public static final String DEFAULT_CATEGORY = "Default category";
	
	private static NewFileRegistry sharedInstance;
	/**
	 * @deprecated Use {@link #sharedInstance()} instead
	 */
	public static NewFileRegistry getSharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new NewFileRegistry();
		return sharedInstance;
	}	
	
	public static NewFileRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new NewFileRegistry();
		return sharedInstance;
	}
	
	protected Map category2Actions = new HashMap();
	public Map getCategory2Actions() {
		checkProcessing();
		return category2Actions;
	}
	
	public String getCategoryName(String categoryID) {
		return categoryRegistry.getCategoryName(categoryID);
	}
	
	protected CategoryRegistry categoryRegistry = new CategoryRegistry();
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	public void processElement(IExtension extension, IConfigurationElement element)
	throws EPProcessorException 
	{
		if (element.getName().equalsIgnoreCase("action")) 
		{
			String className = element.getAttribute("class");
			if (!checkString(className))
				throw new EPProcessorException("Element action has to define attribute class.");
						
			String categoryID = element.getAttribute("categoryID");
			if (!checkString(categoryID) || categoryRegistry.getCategoryName(categoryID) == null)
				categoryID = DEFAULT_CATEGORY;
				
			String fileExtension = element.getAttribute("fileExtension");
			if (!checkString(fileExtension))
				throw new EPProcessorException("Element action has to define attribute fileExtension.");
			
			String title = element.getAttribute("title");
			if (!checkString(title))
				throw new EPProcessorException("Element action has to define attribute title.");

			String tooltip = element.getAttribute("tooltip");			
			String iconName = element.getAttribute("icon");
			
//			try {
//				Class actionClass = Class.forName(className);
//				Object o = actionClass.newInstance();
//				if (o instanceof IAction) {
//					IAction action = (IAction) o;
//					category2Actions.put(categoryID, action);
//				}
//			} catch (ClassNotFoundException e) {				
//				throw new EPProcessorException("Class "+className+" not found!");
//			} catch (InstantiationException e) {
//				throw new EPProcessorException("Class "+className+" can not be instantiated!");
//			} catch (IllegalAccessException e) {
//				throw new EPProcessorException("Class "+className+" can not be accessed!");			
//			}
				
			Object o;
			try {
				o = element.createExecutableExtension("class");
				if (o instanceof INewFileAction) {
					INewFileAction action = (INewFileAction) o;
					action.setFileExtension(fileExtension);
					action.setText(title);
					if (checkString(tooltip))
						action.setToolTipText(tooltip);					
					if (checkString(iconName))
						action.setImageDescriptor(getImageDescriptor(iconName));
					
					category2Actions.put(categoryID, action);
				}					
			} catch (CoreException e) {
				throw new EPProcessorException(e);
			}			
		}
	}

	protected ImageDescriptor getImageDescriptor(String iconName) 
	{
		if (iconName != null) {			
			return ImageDescriptor.createFromURL(NLBasePlugin.getDefault().getBundle().getEntry(iconName));
		}
		return null;
	}
	
	protected boolean checkString(String s) 
	{
		if (s == null || "".equals(s))
			return false;
		
		return true;
	}
	
	protected class CategoryRegistry 
	extends AbstractEPProcessor 
	{ 
		protected Map categoryID2name = new HashMap();
		
		public String getExtensionPointID() {
			return EXTENSION_POINT_ID;
		}
		
		// TODO: implement this
		public String getCategoryName(String categoryID) 
		{
			checkProcessing();
			return (String) categoryID2name.get(categoryID);
		}

		public void processElement(IExtension extension, IConfigurationElement element) 
		throws EPProcessorException 
		{
			if (element.getName().equalsIgnoreCase("category")) 
			{
				String id = element.getAttribute("id");
				if (!checkString(id))
					throw new EPProcessorException("Element category has to define attribute id.");
 				
				String name = element.getAttribute("name");
				if (!checkString(name))
					throw new EPProcessorException("Element category has to define attribute name.");
				
				categoryID2name.put(id, name);
			}
		}
				
	}
	
}