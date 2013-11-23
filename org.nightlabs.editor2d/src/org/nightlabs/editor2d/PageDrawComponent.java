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

import java.awt.Rectangle;


/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface PageDrawComponent
extends DrawComponentContainer
//IResolution
{
//	public static final String PROP_RESOLUTION = "resolution";
	public static final String PROP_CURRENT_LAYER = "currentLayer";
	public static final String PROP_ORIENTATION = "orientation";
	public static final String PROP_PAGE_BOUNDS = "pageBounds";
	public static final String PROP_SHOW_PAGE_BOUNDS = "show pageBounds";
	
	public static int ORIENTATION_HORIZONTAL = 1;
	public static int ORIENTATION_VERTICAL = 2;
		
//	/**
//	 * return the Resolution of the Page in dpi
//	 * @return the Resolution of the Page in dpi
//	 * @see Resolution
//	 */
//	public Resolution getResolution();
//
//	/**
//	 *
//	 * @param resolution the Resolution to set
//	 */
//	public void setResolution(Resolution resolution);
		
	/**
	 * returns the current Layer of the Page
	 * @return the currentLayer of the Page
	 */
	public Layer getCurrentLayer();
	
	/**
	 * sets the new current layer of the page
	 * @param layer the new layer to set
	 */
	public void setCurrentLayer(Layer layer);
	
	/**
	 * returns orientation of the page either {@link PageDrawComponent#ORIENTATION_HORIZONTAL} or
	 * {@link PageDrawComponent#ORIENTATION_VERTICAL}
	 * @return the orientation of the Page
	 */
	int getOrientation();
	
	/**
	 * sets the orientation of the page either {@link PageDrawComponent#ORIENTATION_HORIZONTAL} or
	 * {@link PageDrawComponent#ORIENTATION_VERTICAL}
	 * @param orientation the orientation to set
	 */
	void setOrientation(int orientation);
	
	/**
	 * Use this method instead of getBounds() to get the page bounds
	 * 
	 * returns the bounds of the page
	 * @return the bounds of the page
	 */
	Rectangle getPageBounds();
	
	/**
	 * Use this method instead of setBounds() to set the page bounds
	 * 
	 * sets the bounds of the page
	 * @param pageBounds the bounds of the page to set
	 */
	void setPageBounds(Rectangle pageBounds);
	
	/**
	 * returns if the pageBounds are visible or not
	 * @return if the pageBounds are visible
	 */
	boolean isShowPageBounds();
	
	/**
	 * determines if the pageBounds are visible or not
	 * @param showPageBounds determines if the pageBounds are visible or not
	 */
	void setShowPageBounds(boolean showPageBounds);
}
