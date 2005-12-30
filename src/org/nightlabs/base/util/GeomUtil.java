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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class GeomUtil
{

	public GeomUtil() {
		super();
	}

	public static Point toSWTPoint(Point2D p) {
		return new Point((int) p.getX(), (int) p.getY());
	}

	public static Point2D toPoint2D(int x, int y) {
		return new Point2D.Double(x, y);
	}

	public static Point2D toPoint2D(Point p) {
		return new Point2D.Double(p.x, p.y);
	}	
	
	public static Rectangle2D toRectangle2D(Rectangle r) {
		return new Rectangle2D.Double(r.x, r.y, r.width, r.height);
	}

	public static java.awt.Rectangle toAWTRectangle(Rectangle r) {
		return new java.awt.Rectangle(r.x, r.y, r.width, r.height);
	}
	
	public static Rectangle toSWTRectangle(Rectangle2D r2d) {
		java.awt.Rectangle r = r2d.getBounds();
		return new Rectangle(r.x, r.y, r.width, r.height);
	}
	
	public static Rectangle toSWTRectangle(java.awt.Rectangle r) {
		return new Rectangle(r.x, r.y, r.width, r.height);
	}	
	
	/**
	 * checks if the given source rectangle is contained in the target Rectangle,
	 * if the source rectangle is outside of the target Rectangle the source rectangle is 
	 * trimmed so that it fits in the target rectangle
	 * 
	 * @param source the source rectangle to check 
	 * @param target the target rectangle to check if the source rectangle fits in
	 * @return the modified source rectangle
	 */
	public static java.awt.Rectangle checkBounds(java.awt.Rectangle source, java.awt.Rectangle target) 
	{
		if (target.contains(source))
			return target;
		else 
		{
			// is source outter target left
			if (source.x < target.x) {
				source.x = target.x;
				if (source.width > target.width)
					source.width = target.width;
			}
			// is source outter target top
			if (source.y < target.y) {
				source.y = target.y;
				if (source.height > target.height)
					source.height = target.height;
			}
			// is source outter target right
			if (source.getMaxX() > target.getMaxX()) {
				source.x = (int)target.getMaxX() - source.width;
				if (source.width > target.width)
					source.width = target.width;
			}
			// is source outter target bottom
			if (source.getMaxY() > target.getMaxY()) {
				source.y = (int) target.getMaxY() - source.height;
				if (source.height > target.height)
					source.height = target.height; 
			}
			return source;
		}			
	}
	
	/**
	 * 
	 * @param r the Rectangle to translate to Origin (0/0)
	 * @return the translated source rectangle
	 */
	public static java.awt.Rectangle translateRectToOrigin(java.awt.Rectangle r) 
	{
		if (r.x < 0) {
			r.width = r.width - r.x;
			r.x = 0;
		}
		if (r.x > 0) {
			r.width = r.width + r.x;
			r.x = 0;
		}
		if (r.y < 0) {
			r.height = r.height - r.y;
			r.y = 0;
		}
		if (r.y > 0) {
			r.height = r.height + r.y;
			r.y = 0;
		}
		return r;
	}		
	
	/**
	 * checks if the dimension of target rectangle is contained in the source rectangle,
	 * if the dimension target rectangle is greater it gets the dimension of the source rectangle 
	 * 
	 * @param source the source rectangle
	 * @param target the target rectangle to check 
	 * @retrun the (if necessary) modified target rectangle
	 */
	public static java.awt.Rectangle checkDimension(java.awt.Rectangle source, java.awt.Rectangle target) 
	{
		if (target.width > source.width)
			target.width = source.width;
		
		if (target.height > source.height)
			target.height = source.height;
		
		return target;
	}
	
}
