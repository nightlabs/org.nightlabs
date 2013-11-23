/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.util;

import java.awt.Color;
import java.util.regex.Pattern;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Daniel Mazurek - daniel.mazurek at nightlabs dot de
 */
public class ColorUtil
{

	protected ColorUtil()
	{
	}
	
	protected static class RGBA {
		public int r, g, b, a;
	}

	protected static final Pattern SPLIT_RGBA_PATTERN = Pattern.compile(
			"^\\p{Blank}*|\\p{Blank}*\\{\\p{Blank}*|\\p{Blank}*\\}\\p{Blank}*$|,\\p{Blank}*");

	protected static RGBA parseRGBA(String color)
	{
		RGBA rgba = new RGBA();
		String[] parts = SPLIT_RGBA_PATTERN.split(color);
		if (!"RGBA".equals(parts[1]) && !"RGB".equals(parts[1]))
			throw new IllegalArgumentException("color is neither RGB nor RGBA encoded! It must start with 'RGB' or 'RGBA'!");

		if (parts.length < 5)
			throw new IllegalArgumentException("color is incomplete! It must at least contain three decimal numbers specifying red, green and blue! It should specify alpha as forth value.");

		rgba.r = Integer.parseInt(parts[2]);
		rgba.g = Integer.parseInt(parts[3]);
		rgba.b = Integer.parseInt(parts[4]);

		if (parts.length >= 6)
			rgba.a = Integer.parseInt(parts[5]);
		else
			rgba.a = 255;

		if (rgba.r < 0 || rgba.g < 0 || rgba.b < 0 || rgba.a < 0)
			throw new IllegalArgumentException("color is invalid! One of the values for red, gree, blue or alpha is less than 0!");

		if (rgba.r > 255 || rgba.g > 255 || rgba.b > 255 || rgba.a > 255)
			throw new IllegalArgumentException("color is invalid! One of the values for red, gree, blue or alpha is greater than 255!");

		return rgba;
	}

	public static String colorToString(Color color)
	{
		return "RGBA{"
			+ color.getRed() + ','
			+ color.getGreen() + ','
			+ color.getBlue() + ','
			+ color.getAlpha() + '}';
	}

	/**
	 * @return Returns a String in the form RGBA{255,0,128,255}
	 */
	public static String colorToString(int red, int green, int blue, int alpha)
	{
		return "RGBA{"
			+ red + ','
			+ green + ','
			+ blue + ','
			+ alpha + '}';
	}

	public static Color stringToColor(String color)
	{
		RGBA rgba = parseRGBA(color);
		return new Color(
				rgba.r,
				rgba.g,
				rgba.b,
				rgba.a);
	}

	/**
	 * Darkens the given color corresponding to the given multiplier.
	 * 
	 * @param c the source Color
	 * @param multiplier the factor which determines how much darker the color should be
	 * (how many times {@link Color#darker()} is called)
	 * @return the darkend color
	 */
	public static Color darker(Color c, int multiplier)
	{
		if (multiplier <= 0)
			return c;
		Color returnColor = c;
		for (int i=0; i<multiplier; i++) {
			returnColor = returnColor.darker();
		}
		return returnColor;
	}

	/**
	 * Brightens the given color corresponding to the given multiplier.
	 * 
	 * @param c the source Color
	 * @param multiplier the factor which determines how much brighter the color should be
	 * (how many times {@link Color#brighter()} is called)
	 * @return the brightend color
	 */
	public static Color brighter(Color c, int multiplier)
	{
		if (multiplier <= 0)
			return c;
		Color returnColor = c;
		for (int i=0; i<multiplier; i++) {
			returnColor = returnColor.brighter();
		}
		return returnColor;
	}
	
}
