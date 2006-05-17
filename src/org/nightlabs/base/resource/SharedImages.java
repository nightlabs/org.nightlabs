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

	// TODO: Flag Icons should come from Server
	public static ImageDescriptor getImageDescriptor(String languageID) 
	{
		if (languageID == null)
			return null;
		
		return (ImageDescriptor) languageID2ImageDesc.get(languageID);
	}

	public static enum ImageDimension {
		_16x16 {
			public String toString()
			{
				return "16x16";
			}
		},
		_24x24 {
			public String toString()
			{
				return "24x24";
			}
		},
		_75x70 {
			public String toString()
			{
				return "75x70";
			}
		},
		/**
		 * @deprecated What's that strange format doing here? Marco.
		 */
		_150x15 {
			public String toString()
			{
				return "150x15";
			}
		}
	}

	public static final ImageDimension IMAGE_DIMENSION_WIZARD_PAGE = ImageDimension._75x70;
	public static final ImageDimension IMAGE_DIMENSION_DEFAULT = ImageDimension._16x16;

	public static enum ImageFormat {
		png, gif
	}

	public static final ImageFormat IMAGE_FORMAT_DEFAULT = ImageFormat.png;
	private static final String IMAGES_FOLDER_NAME = "icons";
	
	private Map<String,Image> images = new HashMap<String,Image>();
	private Map<String,ImageDescriptor> imageDescriptors = new HashMap<String,ImageDescriptor>();
	
	
	private static SharedImages sharedInstance;
	
	public static SharedImages sharedInstance() {
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
	private String getImageKey(Plugin plugin, Class clazz, String suffix, String dimension, ImageFormat format) {
		return plugin.getBundle().getSymbolicName() + "/" + getImageSubPath(plugin, clazz, suffix) + "." + dimension + "." + format;
	}

	/**
	 * Get the ImageDescriptor with the given parameters out of the caching Map.  
	 */
	private ImageDescriptor getImageDescriptor(Plugin plugin, Class clazz, String _suffix, String dimension, ImageFormat format) {
		String suffix = (_suffix != null) ? _suffix : "";
		String imageKey = getImageKey(plugin, clazz, suffix, dimension, format);
		ImageDescriptor imageDescriptor = imageDescriptors.get(imageKey);
		if (imageDescriptor == null) {
			imageDescriptor = ImageDescriptor.createFromURL(
					plugin.getBundle().getEntry(
							getImageSubPath(plugin, clazz, suffix) + "." + dimension + "." + format
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
	public Image getImage(Plugin plugin, Class clazz, String _suffix, String dimension, ImageFormat format) {
		String suffix = (_suffix != null) ? _suffix : "";
		String imageKey = getImageKey(plugin, clazz, suffix, dimension, format);
		Image image = (Image) images.get(imageKey);
		if (image == null) {
			ImageDescriptor imageDescriptor = getImageDescriptor(plugin, clazz, _suffix, dimension, format);
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
	 * @param dimension The format of the image given as string in format ("{width}x{height}")
	 * @param format The extension of the image to be loaded (e.g. "png" or "gif")
	 */
	public static Image getSharedImage(Plugin plugin, Class clazz, String suffix, ImageDimension dimension, ImageFormat format) {
		return sharedInstance().getImage(plugin, clazz, suffix, dimension.toString(), format); 
	}

	/**
	 * Get the ImageDescriptor for the given clazz and suffix under the given plugin. The image will be
	 * searched based on the NightLabs coding guidelines.
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 * @param suffix The suffix for the image desired
	 * @param dimension The format of the image given as string in format ("{width}x{height}")
	 * @param format The extension of the image to be loaded (e.g. "png" or "gif")
	 */
	public static ImageDescriptor getSharedImageDescriptor(Plugin plugin, Class clazz, String suffix, ImageDimension dimension, ImageFormat format) {
		return sharedInstance().getImageDescriptor(plugin, clazz, suffix, dimension.toString(), format); 
	}

	/**
	 * Get the ImageDescriptor for the given clazz and suffix under the given plugin. The image will be
	 * searched based on the NightLabs coding guidelines.
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 * @see #getSharedImageDescriptor(Plugin, Class, String, org.nightlabs.base.resource.SharedImages.ImageDimension, org.nightlabs.base.resource.SharedImages.ImageFormat)
	 */
	public static ImageDescriptor getSharedImageDescriptor(Plugin plugin, Class clazz) {
		return sharedInstance().getImageDescriptor(plugin, clazz, null, IMAGE_DIMENSION_DEFAULT.toString(), IMAGE_FORMAT_DEFAULT); 
	}

	/**
	 * Get the ImageDescriptor for the given clazz and suffix under the given plugin. The image will be
	 * searched based on the NightLabs coding guidelines.
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 * @param suffix The suffix for the image desired
	 * @see #getSharedImageDescriptor(Plugin, Class, String, org.nightlabs.base.resource.SharedImages.ImageDimension, org.nightlabs.base.resource.SharedImages.ImageFormat)
	 */
	public static ImageDescriptor getSharedImageDescriptor(Plugin plugin, Class clazz, String suffix) {
		return sharedInstance().getImageDescriptor(plugin, clazz, suffix, IMAGE_DIMENSION_DEFAULT.toString(), IMAGE_FORMAT_DEFAULT); 
	}

	/**
	 * Get the ImageDescriptor for the given clazz and suffix under the given plugin. The image will be
	 * searched based on the NightLabs coding guidelines.
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 * @param suffix The suffix for the image desired
	 * @param dimension The format of the image given as string in format ("{width}x{height}")
	 * @see #getSharedImageDescriptor(Plugin, Class, String, org.nightlabs.base.resource.SharedImages.ImageDimension, org.nightlabs.base.resource.SharedImages.ImageFormat)
	 */	
	public static ImageDescriptor getSharedImageDescriptor(Plugin plugin, Class clazz, String suffix, ImageDimension dimension) {
		return sharedInstance().getImageDescriptor(plugin, clazz, suffix, dimension.toString(), IMAGE_FORMAT_DEFAULT); 
	}
	
	
	/**
	 * Get the ImageDescriptor for a WizardPage of the given clazz and
	 * {@link #IMAGE_DIMENSION_WIZARD_PAGE} and {@link #IMAGE_FORMAT_DEFAULT}. 
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 */
	public static ImageDescriptor getWizardPageImageDescriptor(Plugin plugin, Class clazz) {
		return sharedInstance().getImageDescriptor(plugin, clazz, null, IMAGE_DIMENSION_WIZARD_PAGE.toString(), IMAGE_FORMAT_DEFAULT); 
	}

	/**
	 * Get the image for the given clazz The image will be
	 * searched based on the NightLabs coding guidelines.
	 * This uses <code>sharedInstance().getImage()</code>.
	 * {@link #IMAGE_DIMENSION_DEFAULT} and
	 * {@link #IMAGE_FORMAT_DEFAULT}
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 * @param suffix The suffix for the image
	 */
	public static Image getSharedImage(Plugin plugin, Class clazz, String suffix) {
		return sharedInstance().getImage(plugin, clazz, suffix, IMAGE_DIMENSION_DEFAULT.toString(), IMAGE_FORMAT_DEFAULT); 
	}

	/**
	 * Get the image for the given clazz The image will be
	 * searched based on the NightLabs coding guidelines.
	 * This uses <code>sharedInstance().getImage()</code>.
	 * and passes no suffix, {@link #IMAGE_DIMENSION_DEFAULT} and
	 * {@link #IMAGE_FORMAT_DEFAULT}
	 * 
	 * @param plugin The plugin the image is package in.
	 * @param clazz The class using the image
	 */
	public static Image getSharedImage(Plugin plugin, Class clazz) {
		return sharedInstance().getImage(plugin, clazz, null, IMAGE_DIMENSION_DEFAULT.toString(), IMAGE_FORMAT_DEFAULT); 
	}

	protected static final ImageDescriptor FLAG_USA_16x16 = 
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "USA");

	protected static final ImageDescriptor FLAG_GERMANY_16x16 =
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Germany");		

	protected static final ImageDescriptor FLAG_FRANCE_16x16 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "France");
	
	protected static Map languageID2ImageDesc;

	static {
		languageID2ImageDesc = new HashMap();
		languageID2ImageDesc.put(Locale.ENGLISH.getLanguage(), FLAG_USA_16x16);
		languageID2ImageDesc.put(Locale.GERMAN.getLanguage(), FLAG_GERMANY_16x16);
		languageID2ImageDesc.put(Locale.FRENCH.getLanguage(), FLAG_FRANCE_16x16);		
	}

	public static final ImageDescriptor EDIT_16x16 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Edit");

	public static final ImageDescriptor EDIT_24x24 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Edit",
				ImageDimension._24x24);

	public static final ImageDescriptor ADD_16x16 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Create");
	
	public static final ImageDescriptor ADD_24x24 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Create",
				ImageDimension._24x24);

	public static final ImageDescriptor DELETE_16x16 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Delete");	
	
	public static final ImageDescriptor DELETE_24x24 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Delete",
				ImageDimension._24x24);
		
	public static final ImageDescriptor DISCARD_16x16 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Discard");	

	public static final ImageDescriptor DISCARD_24x24 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Discard",
				ImageDimension._24x24);	
		
	public static final ImageDescriptor PRINT_16x16 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Print");	

	public static final ImageDescriptor PRINT_24x24 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Print",
				ImageDimension._24x24);	
	
	public static final ImageDescriptor SAVE_16x16 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Save");	
	
	public static final ImageDescriptor SAVE_24x24 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Save",
				ImageDimension._24x24);		
	
	public static final ImageDescriptor SEARCH_16x16 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Search");	

	public static final ImageDescriptor SEARCH_24x24 = 		
		getSharedImageDescriptor(NLBasePlugin.getDefault(), NLBasePlugin.class, "Search",
				ImageDimension._24x24);	
}
