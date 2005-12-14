/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.ressource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;

import org.nightlabs.base.NLBasePlugin;

public class SharedImages
{	
	public SharedImages() {
		super();
	}
	
	public static final ImageDescriptor FLAG_USA_16x16 = 
		ImageDescriptor.createFromURL(
				NLBasePlugin.getDefault().getBundle().getEntry(
						NLBasePlugin.getResourceString("icon.flag.english")));
	
	public static final ImageDescriptor FLAG_GERMANY_16x16 = 		
		ImageDescriptor.createFromURL(
			NLBasePlugin.getDefault().getBundle().getEntry(
					NLBasePlugin.getResourceString("icon.flag.german")));

	public static final ImageDescriptor FLAG_FRANCE_16x16 = 		
		ImageDescriptor.createFromURL(
			NLBasePlugin.getDefault().getBundle().getEntry(
					NLBasePlugin.getResourceString("icon.flag.french")));	
	
	protected static Map languageID2ImageDesc;
	
	static {
		languageID2ImageDesc = new HashMap();
		languageID2ImageDesc.put(Locale.ENGLISH.getLanguage(), FLAG_USA_16x16);
		languageID2ImageDesc.put(Locale.GERMAN.getLanguage(), FLAG_GERMANY_16x16);
		languageID2ImageDesc.put(Locale.FRENCH.getLanguage(), FLAG_FRANCE_16x16);		
	}
		
	public static ImageDescriptor getImageDescriptor(String languageID) 
	{
		if (languageID == null)
			return null;
		
		return (ImageDescriptor) languageID2ImageDesc.get(languageID);
	}
	
	public static final ImageDescriptor EDIT_24x24 = 		
		ImageDescriptor.createFromURL(
			NLBasePlugin.getDefault().getBundle().getEntry(
					NLBasePlugin.getResourceString("icon.java.edit")));	

	public static final ImageDescriptor ADD_24x24 = 		
		ImageDescriptor.createFromURL(
			NLBasePlugin.getDefault().getBundle().getEntry(
					NLBasePlugin.getResourceString("icon.java.add")));	

	public static final ImageDescriptor DELETE_24x24 = 		
		ImageDescriptor.createFromURL(
			NLBasePlugin.getDefault().getBundle().getEntry(
					NLBasePlugin.getResourceString("icon.java.delete")));	
	
	
	public static final ImageDescriptor EDIT_16x16 = 		
		ImageDescriptor.createFromURL(
			NLBasePlugin.getDefault().getBundle().getEntry(
					NLBasePlugin.getResourceString("icon.edit")));	

	public static final ImageDescriptor ADD_16x16 = 		
		ImageDescriptor.createFromURL(
			NLBasePlugin.getDefault().getBundle().getEntry(
					NLBasePlugin.getResourceString("icon.add")));	

	public static final ImageDescriptor DELETE_16x16 = 		
		ImageDescriptor.createFromURL(
			NLBasePlugin.getDefault().getBundle().getEntry(
					NLBasePlugin.getResourceString("icon.delete")));		
}
