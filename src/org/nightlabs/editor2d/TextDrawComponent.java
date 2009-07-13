/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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

package org.nightlabs.editor2d;

import java.awt.Font;

public interface TextDrawComponent
extends ShapeDrawComponent, IText
{
	public static final String PROP_TEXT = "text";
	public static final String PROP_FONT_SIZE = "fontSize";
	public static final String PROP_BOLD = "bold";
	public static final String PROP_ITALIC = "italic";
	public static final String PROP_FONT_NAME = "fontName";
	public static final String PROP_FONT = "font";
	
  public static final int FONT_SIZE_DEFAULT = 12;
  public static final boolean BOLD_DEFAULT = false;
  public static final boolean ITALIC_DEFAULT = false;
  public static final String FONT_NAME_DEFAULT = null;
  public static final Font FONT_DEFAULT = new Font("Arial", Font.PLAIN, 12);
  
//  String getText();
//  void setText(String value);

  int getFontSize();
  void setFontSize(int value);
  
  boolean isBold();
  void setBold(boolean value);

  boolean isItalic();
  void setItalic(boolean value);

  String getFontName();
  void setFontName(String value);

  Font getFont();
  void setFont(Font value);

  boolean isTransformed();
} // TextDrawComponent
