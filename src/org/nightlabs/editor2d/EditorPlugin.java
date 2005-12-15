package org.nightlabs.editor2d;

import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class EditorPlugin 
extends AbstractUIPlugin 
{
	//The shared instance.
	private static EditorPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	public static String PLUGIN_ID = "org.nightlabs.editor2d";
	
	/**
	 * The constructor.
	 */
	public EditorPlugin() {
		super();
		plugin = this;
		try {
		  resourceBundle = ResourceBundle.getBundle("org.nightlabs.editor2d.plugin");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static EditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = EditorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() 
	{
	  if (resourceBundle == null) 
	  {
			try {
			  resourceBundle = ResourceBundle.getBundle("org.nightlabs.editor2d.plugin");
			} catch (MissingResourceException x) {
				resourceBundle = null;
			}	  	    
	  }
		return resourceBundle;
	}
}
