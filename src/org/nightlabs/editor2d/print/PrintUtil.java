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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;

import org.apache.log4j.Logger;
import org.nightlabs.base.util.GeomUtil;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.page.unit.ResolutionUnit;
import org.nightlabs.editor2d.util.UnitUtil;
import org.nightlabs.i18n.IUnit;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class PrintUtil 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(PrintUtil.class);
	
	public PrintUtil() {
		super();
	}

	public static void logPageFormat(PageFormat pf) 
	{
		logger.debug("PageFormat Width = "+pf.getWidth());
		logger.debug("PageFormat Height = "+pf.getHeight());		
		logger.debug("PageFormat ImageableX = "+pf.getImageableX());
		logger.debug("PageFormat ImageableY = "+pf.getImageableY());
		logger.debug("PageFormat ImageableWidth = "+pf.getImageableWidth());
		logger.debug("PageFormat ImageableHeight = "+pf.getImageableHeight());
		logger.debug("PageFormat Orientation = "+getOrientationAsString(pf.getOrientation()));	
		logger.debug("");
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
	
//	public static void prepareGraphics(Graphics2D g2d, DrawComponent dc, PageFormat pageFormat) 
//	{
//		if (logger.isDebugEnabled())
//			logger.debug("dc.getBounds() = "+dc.getBounds());
//		
//		Rectangle dcBounds = GeomUtil.translateToOrigin(dc.getBounds());
//		
//		if (logger.isDebugEnabled())
//			logger.debug("originBounds = "+dcBounds);
//		
////		Point2D resolutionScale = UnitUtil.getResolutionScale(72, dc);
////		GeneralShape gs = new GeneralShape(dcBounds);
////		AffineTransform scaleAT = AffineTransform.getScaleInstance(resolutionScale.getX(), resolutionScale.getY());
////		gs.transform(scaleAT);
////		dcBounds = gs.getBounds();
////		
////		if (logger.isDebugEnabled()) {
////			logger.debug("resolutionScale = "+resolutionScale);
////			logger.debug("dcBounds = "+dcBounds);
////		}
//		
//		Rectangle pageRectangle = new Rectangle(0, 0, 
//				(int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());		
//		
//		Point2D scales = GeomUtil.calcScale(dcBounds, pageRectangle);		
//		double scale = Math.min(scales.getX(), scales.getY()); 
//		double translateX = (((double)pageFormat.getImageableX()) - (dc.getX() * scale ));
//		double translateY = (((double)pageFormat.getImageableY()) - (dc.getY() * scale )); 		
//		g2d.translate(translateX, translateY);		
//		g2d.scale(scale, scale);
//		
//		if (logger.isDebugEnabled()) {
//			logger.debug("pageRectangle = "+pageRectangle);
//			logger.debug("scale = "+scale);
//			logger.debug("translateX = "+translateX);
//			logger.debug("translateY = "+translateY);			
//		}
//	}	
//
//	public static void prepareGraphics(Graphics2D g2d, PageDrawComponent page, PageFormat pageFormat) 
//	{
//		if (logger.isDebugEnabled())
//			logger.debug("page.getBounds() = "+page.getBounds());
//		
//		Rectangle pageBounds = GeomUtil.translateToOrigin(page.getBounds());
//		
//		if (logger.isDebugEnabled())
//			logger.debug("originBounds = "+pageBounds);
//				
//		Rectangle pageRectangle = new Rectangle(0, 0, 
//				(int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());		
//		
//		Point2D scales = GeomUtil.calcScale(pageBounds, pageRectangle);		
//		double scale = Math.min(scales.getX(), scales.getY()); 
//		double translateX = (((double)pageFormat.getImageableX()) - (page.getX() * scale ));
//		double translateY = (((double)pageFormat.getImageableY()) - (page.getY() * scale )); 		
//		g2d.translate(translateX, translateY);		
//		g2d.scale(scale, scale);
//		
//		if (logger.isDebugEnabled()) {
//			logger.debug("pageRectangle = "+pageRectangle);
//			logger.debug("scale = "+scale);
//			logger.debug("translateX = "+translateX);
//			logger.debug("translateY = "+translateY);			
//		}
//	}	
	
}
