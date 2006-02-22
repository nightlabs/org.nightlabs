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

package org.nightlabs.editor2d.print;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;

import org.apache.log4j.Logger;
import org.nightlabs.base.util.GeomUtil;
import org.nightlabs.editor2d.DrawComponent;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class PrintUtil 
{
	public static final Logger LOGGER = Logger.getLogger(PrintUtil.class);
	
	public PrintUtil() {
		super();
	}

	public static void logPageFormat(PageFormat pf) 
	{
		LOGGER.debug("PageFormat Width = "+pf.getWidth());
		LOGGER.debug("PageFormat Height = "+pf.getHeight());		
		LOGGER.debug("PageFormat ImageableX = "+pf.getImageableX());
		LOGGER.debug("PageFormat ImageableY = "+pf.getImageableY());
		LOGGER.debug("PageFormat ImageableWidth = "+pf.getImageableWidth());
		LOGGER.debug("PageFormat ImageableHeight = "+pf.getImageableHeight());
		LOGGER.debug("PageFormat Orientation = "+getOrientationAsString(pf.getOrientation()));	
		LOGGER.debug("");
	}
	
	protected static String getOrientationAsString(int orientation) 
	{
		switch (orientation) 
		{
			case(PageFormat.LANDSCAPE):
				return "Landscape";
			case(PageFormat.PORTRAIT):
				return "Portrait";
			case(PageFormat.REVERSE_LANDSCAPE):
				return "Reverse Landscape";						
		}
		return "No valid orientation";
	}	
	
	public static void prepareGraphics(Graphics2D g2d, DrawComponent dc, PageFormat pageFormat) 
	{
		Rectangle dcBounds = dc.getBounds();
//		LOGGER.debug("dcBounds before translate = "+dcBounds);		
		dcBounds = GeomUtil.translateRectToOrigin(dcBounds);
//		LOGGER.debug("dcBounds after translate = "+dcBounds);		
		
		Rectangle pageRectangle = new Rectangle(0, 0, 
				(int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());		
//		LOGGER.debug("pageRectangle = "+ pageRectangle);
		
		double scaleX = 1;
		double scaleY = 1;
		if (dcBounds.width != 0 && pageRectangle.width != 0)
			scaleX = (double)pageRectangle.width / (double)dcBounds.width;
		if (dcBounds.height != 0 && pageRectangle.height != 0)
			scaleY = (double)pageRectangle.height / (double)dcBounds.height;
		
		double scale = Math.min(scaleX, scaleY); 
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());		
		g2d.scale(scale, scale);
		
//		LOGGER.debug("scaleX = " + scaleX);
//		LOGGER.debug("scaleY = " + scaleY);
//		LOGGER.debug("scale = " + scale);
	}	
}
