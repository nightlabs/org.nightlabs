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

package org.nightlabs.editor2d.impl;

import java.awt.Font;

import org.nightlabs.editor2d.DrawComponentContainer;

public class TextDrawComponentImpl
extends AbstractTextDrawComponent
{
	private static final long serialVersionUID = 1L;

	// Standard Constructor for Editor2DFactory
  public TextDrawComponentImpl() {
  	super();
  }
  
  public TextDrawComponentImpl(String text, Font font, int x, int y,
  		DrawComponentContainer parent)
  {
  	this(text, font.getName(), font.getSize(), font.getStyle(), x, y, parent);
  }
    
  public TextDrawComponentImpl(String text, String fontName, int fontSize, int fontStyle,
  		int x, int y, DrawComponentContainer parent)
  {
  	super(text, fontName, fontSize, fontStyle, x, y, parent);
  }
  
  private String text;

	@Override
  public String getInternalText() {
		return text;
	}
	@Override
  public void setInternalText(String text) {
  	this.text = text;
  }
    
} //TextDrawComponentImpl
