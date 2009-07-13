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

import java.awt.GraphicsEnvironment;
/**
 * A utility class for dealing with fonts
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class FontUtil
{
	protected FontUtil() {
		super();
	}

//	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 24);
//	public static final Font DEFAULT_FONT = UIManager.getDefaults().getFont(new Label().getFont());
//	public static final Font DEFAULT_FONT = new Label().getFont();
//	public static Font getDefaultFont() {
//		return new Label().getFont();
//	}
	
  private static String[] fontSizes = new String[] {"8","10","12","14","16","18","24","30","36","48"};
  
  /**
   * returns a String[] with predefined font sizes from 8 to 48
   * @return a String[] with predefined font sizes
   */
  public static String[] getFontSizes() {
    return fontSizes;
  }
  
  private static String[] systemFonts;
  /**
   * returns a String[] with all available system fonts
   * @return a String[] with all available system fonts
   */
  public static String[] getSystemFonts()
  {
    if (systemFonts == null) {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      systemFonts = ge.getAvailableFontFamilyNames();
    }
    return systemFonts;
  }
  
//  public static void initSystemFonts() {
//    getSystemFonts();
//  }
  
  /**
   * checks if the font with the given name is available
   * @return true if yes, false if not
   */
  public static boolean isFontAvailable(String fontName)
  {
    for (int j = 0; j < getSystemFonts().length; j++) {
    	String f = getSystemFonts()[j];
    	if (f.equals(fontName)) {
    		return true;
    	}
    }
    return false;
  }
}
