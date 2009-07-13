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
package org.nightlabs.editor2d.render;

import java.awt.Color;
import java.awt.Font;

/**
 * This Interface determines if a String should be rendered and if so, with
 * which font and with wich fontColor
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface StringRenderer
//extends Renderer
{
	/**
	 * 
	 * @return true if the String should be rendered
	 */
	boolean isShowString();
	
	/**
	 * 
	 * @param showString determines if the String should be rendered or not
	 */
	void setShowString(boolean showString);
		
	/**
	 * 
	 * @return the Font of the rendered String
	 */
	Font getFont();
	
	/**
	 * 
	 * @param f the Font to set for the rendered String
	 */
	void setFont(Font f);
	
	/**
	 * 
	 * @return the Color of the Font
	 */
	Color getFontColor();
	
	/**
	 * 
	 * @param fontColor the Color of the Font to set
	 */
	void setFontColor(Color fontColor);
}
