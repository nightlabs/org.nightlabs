/*
 * Created on Jan 7, 2005
 */
package org.nightlabs.base.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ColorUtil extends org.nightlabs.util.ColorUtil
{

	protected ColorUtil()
	{
	}

	public static String rgbToString(RGB rgb)
	{
		return "RGBA{"
				+ rgb.red + ','
				+ rgb.green + ','
				+ rgb.blue + ",255}"; // SWT RGB doesn't know alpha. We set it to 255 even though we could leave it out. 
	}

	public static RGB stringToRGB(String color)
	{
		RGBA rgba = parseRGBA(color);
		return new RGB(rgba.r, rgba.g, rgba.b);
	}

	public static String swtColorToString(Color color)
	{
		return "RGBA{"
		+ color.getRed() + ','
		+ color.getGreen() + ','
		+ color.getBlue() + ",255}"; // SWT Color doesn't know alpha. We set it to 255 even though we could leave it out.
	}

	/**
	 * <b>Important:</b> You must dispose the returned color when you don't need it
	 * anymore!
	 */
	public static Color stringToSWTColor(String color)
	{
		RGBA rgba = parseRGBA(color);
		return new Color(Display.getDefault(), rgba.r, rgba.g, rgba.b);
	}

	/**
	 * <b>Important:</b> You must dispose the returned color when you don't need it
	 * anymore!
	 */
	public static Color stringToSWTColor(Device device, String color)
	{
		RGBA rgba = parseRGBA(color);
		if (device == null)
			device = Display.getDefault();
		return new Color(device, rgba.r, rgba.g, rgba.b);
	}
	
	/**
	 * A utility method to convert an SWT Color to an AWT one.
	 * 
	 * @param c
	 *            The SWT Color
	 * @return An equivalent AWT Color
	 */
	public static java.awt.Color toAWTColor(Color c) {
		return new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue());
	}
	
	/**
	 * A utility method to convert an AWT Color to an SWT one.
	 * The default display is used.
	 * Resource disposal should be performed by the caller.
	 * 
	 * @param c
	 *            The AWT Color
	 * @return An equivalent SWT Color
	 */
	public static Color toSWTColor(java.awt.Color c) {
		return new Color(Display.getDefault(),c.getRed(), c.getGreen(), c.getBlue());
	}
	
	public static java.awt.Color toAWTColor(RGB rgb) 
	{
	  if (rgb == null)
      throw new IllegalArgumentException("Param rgb must not be null!");
	  return new java.awt.Color(rgb.red, rgb.green, rgb.blue);
	}
	
	public static RGB toRGB(java.awt.Color c) 
	{
	  if(c == null)
	    throw new IllegalArgumentException("Param c must not be null!");
	    
	  return new RGB(c.getRed(), c.getGreen(), c.getBlue());
	}	
}
