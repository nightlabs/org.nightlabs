/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.resource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

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
	
	
	
	public static final String WIZARD_PAGE_IMAGE_FROMAT = "75x70";
	
	public static final String DEFAULT_IMAGE_FORMAT = "16x16";
	public static final String DEFAULT_IMAGE_EXTENSION = "png";
	private static final String IMAGES_FOLDER_NAME = "icons";
	
	private Map images = new HashMap();
	private Map imageDescriptors = new HashMap();
	
	
	private static SharedImages sharedInstance;
	
	protected static SharedImages sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new SharedImages();
		return sharedInstance;
	}
	
	/**
	 * Get the image subpath on basis of the NightLabs coding guidelines
	 */
	private String getImageSubPath(Plugin plugin, Class clazz, String suffix) {
		String pluginNameSpace = plugin.getBundle().getSymbolicName();
		String className = clazz.getName();
		if (!className.contains(pluginNameSpace))
			throw new IllegalArgumentException("Could not extract image sub path for "+className+" in plugin "+pluginNameSpace);
		String subPath = className.replace(pluginNameSpace, "");
		if (subPath.startsWith("."))
			subPath = subPath.replaceFirst("\\.","");
		subPath = subPath.replaceAll("\\.", "/");
		String suffixStr = "-" + suffix;		
		if ((suffix == null) || ("".equals(suffix)))
			suffixStr = "";
		return IMAGES_FOLDER_NAME+"/"+subPath + suffixStr; 
	}

	/**
	 * Get the String key for the image with given parameters 
	 */
	private String getImageKey(Plugin plugin, Class clazz, String suffix, String format, String extension) {
		return plugin.getBundle().getSymbolicName() + "/" + getImageSubPath(plugin, clazz, suffix) + "." + format + "." + extension;
	}

	/**
	 * Get the ImageDescriptor with the given parameters out of the caching Map.  
	 */
	private ImageDescriptor getImageDescriptor(Plugin plugin, Class clazz, String _suffix, String format, String extension) {
		String suffix = (_suffix != null) ? _suffix : "";
		String imageKey = getImageKey(plugin, clazz, suffix, format, extension);
		ImageDescriptor imageDescriptor = (ImageDescriptor) imageDescriptors.get(imageKey);
		if (imageDescriptor == null) {
			imageDescriptor = ImageDescriptor.createFromURL(
					plugin.getBundle().getEntry(
							getImageSubPath(plugin, clazz, suffix) + "." + format + "." + extension
						)
				);
			Image image = imageDescriptor.createImage() ;
			imageDescriptors.put(imageKey, imageDescriptor);
			images.put(imageKey, image);
		}			
		return imageDescriptor;
	}
	
	/**
	 * Get the Image with the given parameters out of the caching Map.  
	 */
	private Image getImage(Plugin plugin, Class clazz, String _suffix, String format, String extension) {
		String suffix = (_suffix != null) ? _suffix : "";
		String imageKey = getImageKey(plugin, clazz, suffix, format, extension);
		Image image = (Image) images.get(imageKey);
		if (image == null) {
			ImageDescriptor imageDescriptor = getImageDescriptor(plugin, clazz, _suffix, format, extension);
			image = imageDescriptor.createImage();
			images.put(imageKey, image);
		}			
		return image;
	}
	
	/**
	 * Get the image for the given clazz and suffix under the given plugin. The image will be
	 * searched based on the NightLabs coding guidelines.
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 * @param suffix The suffix for the image desired
	 * @param format The format of the image given as string in format ("{width}x{height}")
	 * @param extension The extension of the image to be loaded (e.g. "png" or "gif")
	 */
	public static Image getSharedImage(Plugin plugin, Class clazz, String suffix, String format, String extension) {
		return sharedInstance().getImage(plugin, clazz, suffix, format, extension); 
	}
	
	/**
	 * Get the ImageDescriptor for the given clazz and suffix under the given plugin. The image will be
	 * searched based on the NightLabs coding guidelines.
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 * @param suffix The suffix for the image desired
	 * @param format The format of the image given as string in format ("{width}x{height}")
	 * @param extension The extension of the image to be loaded (e.g. "png" or "gif")
	 */
	public static ImageDescriptor getSharedImageDescriptor(Plugin plugin, Class clazz, String suffix, String format, String extension) {
		return sharedInstance().getImageDescriptor(plugin, clazz, suffix, format, extension); 
	}
	
	/**
	 * Get the ImageDescriptor for a WizardPage of the given clazz and
	 * {@link #WIZARD_PAGE_IMAGE_FROMAT} and {@link #DEFAULT_IMAGE_EXTENSION}. 
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 */
	public static ImageDescriptor getWizardPageImageDescriptor(Plugin plugin, Class clazz) {
		return sharedInstance().getImageDescriptor(plugin, clazz, null, WIZARD_PAGE_IMAGE_FROMAT, DEFAULT_IMAGE_EXTENSION); 
	}
	
	/**
	 * Get the image for the given clazz The image will be
	 * searched based on the NightLabs coding guidelines.
	 * This uses {@link #getSharedImage(Plugin, Class, String, String, String)}
	 * {@link #DEFAULT_IMAGE_FORMAT} and
	 * {@link #DEFAULT_IMAGE_EXTENSION}
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 * @param suffix The suffix for the image
	 */
	public static Image getSharedImage(Plugin plugin, Class clazz, String suffix) {
		return sharedInstance().getImage(plugin, clazz, suffix, DEFAULT_IMAGE_FORMAT, DEFAULT_IMAGE_EXTENSION); 
	}
	
	/**
	 * Get the image for the given clazz The image will be
	 * searched based on the NightLabs coding guidelines.
	 * This uses {@link #getSharedImage(Plugin, Class, String, String, String)}
	 * and passes no suffix, {@link #DEFAULT_IMAGE_FORMAT} and
	 * {@link #DEFAULT_IMAGE_EXTENSION}
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 */
	public static Image getSharedImage(Plugin plugin, Class clazz) {
		return sharedInstance().getImage(plugin, clazz, null, DEFAULT_IMAGE_FORMAT, DEFAULT_IMAGE_EXTENSION); 
	}
	
}
