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

package org.nightlabs.base.util;

import java.awt.print.PageFormat;

import org.apache.log4j.Logger;

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
}
