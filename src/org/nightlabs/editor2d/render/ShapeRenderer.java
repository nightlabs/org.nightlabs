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

/**
 * This Interface determines if either the fillColor of ShapeDrawComponents or
 * the lineColor of both should be rendered
 * 
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface ShapeRenderer
//extends Renderer
{
	/**
	* @return true if the LineColor should be rendered
	*/
	boolean isShowLineColor();
	
	/**
	* @param showColor determines if the LineColor should be rendered or not
	*/
	void setShowLineColor(boolean showColor);
	
	/**
	* @return true if the FillColor should be rendered
	*/
	boolean isShowFillColor();
	
	/**
	* @param showColor determines if the LineColor should be rendered or not
	*/
	void setShowFillColor(boolean showColor);
}
