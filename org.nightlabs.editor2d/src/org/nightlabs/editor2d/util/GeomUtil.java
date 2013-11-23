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
package org.nightlabs.editor2d.util;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * A Utility class which provides useful Methods for geometric operations
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class GeomUtil
{
//	/**
//	 * LOG4J logger used by this class
//	 */
//	private static final Logger logger = Logger.getLogger(GeomUtil.class);
	
	public GeomUtil() {
		super();
	}

	private static AffineTransform at = new AffineTransform();
	
	/**
	 * creates a AffineTransform which transforms the second Rectangle (x2, y2, w2, h2)
	 * into the first Rectangle (x1, y1, w1, h1)
	 * 
	 * @param x1 the x-Coordinate of the first rectangle
	 * @param y1 the y-Coordinate of the first rectangle
	 * @param w1 the width of the first rectangle
	 * @param h1 the height of the first rectangle
	 * @param x2 the x-Coordinate of the second rectangle
	 * @param y2 the y-Coordinate of the second rectangle
	 * @param w2 the width of the second rectangle
	 * @param h2 the height of the second rectangle
	 * @return a AffineTransform which represents the transformation from the second
	 * rectangle into the first rectangle
	 * 
	 * @see AffineTransform
	 */
  public static AffineTransform getAffineTransform(int x1, int y1, int w1, int h1,
      int x2, int y2, int w2, int h2)
  {
    // if both Rectangles are equal do nothing
    if (x1 == x2 && y1 == y2 && w1 == w2 && h1 == h2)
    {
//      LOGGER.debug("Both Rectangles are Equal!");
      at.setToIdentity();
      return at;
    }
          
    // if only a Translation is performed, just translate
    if (w1 == w2 && h1 == h2)
    {
//      LOGGER.debug("Only Translation!");
      at.setToIdentity();
      at.translate(x2 - x1, y2 - y1);
      return at;
    }
    else if (x1 == x2 && y1 == y2)
    {
//      LOGGER.debug("Only Scale");
      at.setToIdentity();
      float ratioY = (float)h2 / (float)h1;
      float ratioX = (float)w2 / (float)w1;
      float distanceX = x1 - (x1*ratioX);
      float distanceY = y1 - (y1*ratioY);
      at.translate(distanceX, distanceY);
      at.scale(ratioX, ratioY);
      return at;
    }
    else
    {
//      LOGGER.debug("Scale + Translation");
      // translate to origin and scale
      double ratioX = ((double)w2) / ((double)w1);
      double ratioY = ((double)h2) / ((double)h1);
      double x = x1;
      double y = y1;
      double distanceX = x - (x*ratioX);
      double distanceY = y - (y*ratioY);
      at.setToIdentity();
      at.translate(distanceX, distanceY);
      at.scale(ratioX, ratioY);
      
      // translate back
      AffineTransform at2 = new AffineTransform();
      distanceX = x2 - x1;
      distanceY = y2 - y1;
      at2.translate(distanceX, distanceY);
      
      at.preConcatenate(at2);
    }
    return at;
  }
	
  /**
   * creates a AffineTransform which transforms the second Rectangle (newBounds)
	 * into the first Rectangle (oldBounds)
   * 
   * @param oldBounds the target Rectangle
   * @param newBounds the source Rectangle
   * @return a AffineTransform which represents the transformation from the newBounds
   * into oldBounds
   */
  public static AffineTransform getAffineTransform(Rectangle oldBounds, Rectangle newBounds)
  {
    return getAffineTransform(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
        newBounds.x, newBounds.y, newBounds.width, newBounds.height);
  }
  
	/**
	 * checks if the given source rectangle is contained in the target Rectangle,
	 * if the source rectangle is outside of the target Rectangle,
	 * the returned rectangle is trimmed so that it fits in the target rectangle
	 * 
	 * @param source the source rectangle to check
	 * @param target the target rectangle to check if the source rectangle fits in
	 * @return the a new modified rectangle that it fits in the target rectangle
	 */
	public static Rectangle checkBounds(Rectangle source, Rectangle target)
	{
		if (target.contains(source))
			return new Rectangle(target);
		else
		{
			Rectangle trimmedSource = new Rectangle(source);
			// is source outter target left
			if (trimmedSource.x < target.x) {
				trimmedSource.x = target.x;
				if (trimmedSource.width > target.width)
					trimmedSource.width = target.width;
			}
			// is source outter target top
			if (trimmedSource.y < target.y) {
				trimmedSource.y = target.y;
				if (trimmedSource.height > target.height)
					trimmedSource.height = target.height;
			}
			// is source outter target right
			if (trimmedSource.getMaxX() > target.getMaxX()) {
				trimmedSource.x = (int)target.getMaxX() - trimmedSource.width;
				if (trimmedSource.width > target.width)
					trimmedSource.width = target.width;
			}
			// is source outter target bottom
			if (trimmedSource.getMaxY() > target.getMaxY()) {
				trimmedSource.y = (int) target.getMaxY() - trimmedSource.height;
				if (trimmedSource.height > target.height)
					trimmedSource.height = target.height;
			}
			return trimmedSource;
		}
	}
		
	/**
	 * @param rect the Rectangle to translate to Origin (0/0) and adjust the size
	 * @return a new Rectangle with the origin at (0/0) and newWidth = (oldWidth + oldX),
	 * newHeight = (oldHeight + oldY), which means that the size of the returned rectangle
	 * is always greater than before, if the oldOrigin wasn't already at (0/0)
	 */
	public static Rectangle translateToOriginAndAdjustSize(Rectangle rect)
	{
		java.awt.Rectangle r = new java.awt.Rectangle(rect);
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
	 * calculates the scaleFactor for width and height by r2 / r1 and returns it
	 * as Point2D(scaleX, scaleY)
	 * 
	 * @param r1 Rectangle r1
	 * @param r2 Rectangle r2
	 * @return the scaleFactors scaleX and scaleY as Point2D
	 */
	public static Point2D calcScale(Rectangle r1, Rectangle r2)
	{
		double scaleX = 1.0;
		double scaleY = 1.0;
		if (r1.width != 0 && r2.width != 0)
			scaleX = (double)r2.width / (double)r1.width;
		if (r1.height != 0 && r2.height != 0)
			scaleY = (double)r2.height / (double)r1.height;
		
		return new Point2D.Double(scaleX, scaleY);
	}
	
	/**
	 * 
	 * @param r the Rectangle to translate to Origin (0/0)
	 * @return a new translated rectangle
	 */
	public static Rectangle translateToOrigin(Rectangle r)
	{
		Rectangle newRect = new Rectangle(r);
		newRect.x = 0;
		newRect.y = 0;
		return newRect;
	}
	
	/**
	 * checks if the dimension of target rectangle is contained in the source rectangle,
	 * if the dimension target rectangle is greater it gets the dimension of the source rectangle
	 * 
	 * @param source the source rectangle
	 * @param target the target rectangle to check
	 * @return the (if necessary) modified target rectangle
	 */
	public static Rectangle checkDimension(Rectangle source, Rectangle target)
	{
		if (target.width > source.width)
			target.width = source.width;
		
		if (target.height > source.height)
			target.height = source.height;
		
		return target;
	}
	
  /**
   * Calculates the number of times the line from (x0,y0) to (x1,y1)
   * crosses the ray extending to the right from (px,py).
   * If the point lies on the line, then no crossings are recorded.
   * +1 is returned for a crossing where the Y coordinate is increasing
   * -1 is returned for a crossing where the Y coordinate is decreasing
   */
  public static int pointCrossingsForLine(double px, double py,
                                          double x0, double y0,
                                          double x1, double y1)
  {
      if (py <  y0 && py <  y1) return 0;
      if (py >= y0 && py >= y1) return 0;
      // assert(y0 != y1);
      if (px >= x0 && px >= x1) return 0;
      if (px <  x0 && px <  x1) return (y0 < y1) ? 1 : -1;
      double xintercept = x0 + (py - y0) * (x1 - x0) / (y1 - y0);
      if (px >= xintercept) return 0;
      return (y0 < y1) ? 1 : -1;
  }

  /**
   * Calculates the number of times the quad from (x0,y0) to (x1,y1)
   * crosses the ray extending to the right from (px,py).
   * If the point lies on a part of the curve,
   * then no crossings are counted for that intersection.
   * the level parameter should be 0 at the top-level call and will count
   * up for each recursion level to prevent infinite recursion
   * +1 is added for each crossing where the Y coordinate is increasing
   * -1 is added for each crossing where the Y coordinate is decreasing
   */
  public static int pointCrossingsForQuad(double px, double py,
                                          double x0, double y0,
                                          double xc, double yc,
                                          double x1, double y1, int level)
  {
      if (py <  y0 && py <  yc && py <  y1) return 0;
      if (py >= y0 && py >= yc && py >= y1) return 0;
      // Note y0 could equal y1...
      if (px >= x0 && px >= xc && px >= x1) return 0;
      if (px <  x0 && px <  xc && px <  x1) {
          if (py >= y0) {
              if (py < y1) return 1;
          } else {
              // py < y0
              if (py >= y1) return -1;
          }
          // py outside of y01 range, and/or y0==y1
          return 0;
      }
      // double precision only has 52 bits of mantissa
      if (level > 52) return pointCrossingsForLine(px, py, x0, y0, x1, y1);
      double x0c = (x0 + xc) / 2;
      double y0c = (y0 + yc) / 2;
      double xc1 = (xc + x1) / 2;
      double yc1 = (yc + y1) / 2;
      xc = (x0c + xc1) / 2;
      yc = (y0c + yc1) / 2;
      if (Double.isNaN(xc) || Double.isNaN(yc)) {
          // [xy]c are NaN if any of [xy]0c or [xy]c1 are NaN
          // [xy]0c or [xy]c1 are NaN if any of [xy][0c1] are NaN
          // These values are also NaN if opposing infinities are added
          return 0;
      }
      return (pointCrossingsForQuad(px, py,
                                    x0, y0, x0c, y0c, xc, yc,
                                    level+1) +
              pointCrossingsForQuad(px, py,
                                    xc, yc, xc1, yc1, x1, y1,
                                    level+1));
  }

  /**
   * Calculates the number of times the cubic from (x0,y0) to (x1,y1)
   * crosses the ray extending to the right from (px,py).
   * If the point lies on a part of the curve,
   * then no crossings are counted for that intersection.
   * the level parameter should be 0 at the top-level call and will count
   * up for each recursion level to prevent infinite recursion
   * +1 is added for each crossing where the Y coordinate is increasing
   * -1 is added for each crossing where the Y coordinate is decreasing
   */
  public static int pointCrossingsForCubic(double px, double py,
                                           double x0, double y0,
                                           double xc0, double yc0,
                                           double xc1, double yc1,
                                           double x1, double y1, int level)
  {
      if (py <  y0 && py <  yc0 && py <  yc1 && py <  y1) return 0;
      if (py >= y0 && py >= yc0 && py >= yc1 && py >= y1) return 0;
      // Note y0 could equal yc0...
      if (px >= x0 && px >= xc0 && px >= xc1 && px >= x1) return 0;
      if (px <  x0 && px <  xc0 && px <  xc1 && px <  x1) {
          if (py >= y0) {
              if (py < y1) return 1;
          } else {
              // py < y0
              if (py >= y1) return -1;
          }
          // py outside of y01 range, and/or y0==yc0
          return 0;
      }
      // double precision only has 52 bits of mantissa
      if (level > 52) return pointCrossingsForLine(px, py, x0, y0, x1, y1);
      double xmid = (xc0 + xc1) / 2;
      double ymid = (yc0 + yc1) / 2;
      xc0 = (x0 + xc0) / 2;
      yc0 = (y0 + yc0) / 2;
      xc1 = (xc1 + x1) / 2;
      yc1 = (yc1 + y1) / 2;
      double xc0m = (xc0 + xmid) / 2;
      double yc0m = (yc0 + ymid) / 2;
      double xmc1 = (xmid + xc1) / 2;
      double ymc1 = (ymid + yc1) / 2;
      xmid = (xc0m + xmc1) / 2;
      ymid = (yc0m + ymc1) / 2;
      if (Double.isNaN(xmid) || Double.isNaN(ymid)) {
          // [xy]mid are NaN if any of [xy]c0m or [xy]mc1 are NaN
          // [xy]c0m or [xy]mc1 are NaN if any of [xy][c][01] are NaN
          // These values are also NaN if opposing infinities are added
          return 0;
      }
      return (pointCrossingsForCubic(px, py,
                                     x0, y0, xc0, yc0,
                                     xc0m, yc0m, xmid, ymid, level+1) +
              pointCrossingsForCubic(px, py,
                                     xmid, ymid, xmc1, ymc1,
                                     xc1, yc1, x1, y1, level+1));
  }
  
  /**
   * Calculates the number of times the given path
   * crosses the ray extending to the right from (px,py).
   * If the point lies on a part of the path,
   * then no crossings are counted for that intersection.
   * +1 is added for each crossing where the Y coordinate is increasing
   * -1 is added for each crossing where the Y coordinate is decreasing
   * The return value is the sum of all crossings for every segment in
   * the path.
   * The path must start with a SEG_MOVETO, otherwise an exception is
   * thrown.
   * The caller must check p[xy] for NaN values.
   * The caller may also reject infinite p[xy] values as well.
   */
  public static int pointCrossingsForPath(PathIterator pi,
  		double px, double py)
  {
  	if (pi.isDone()) {
  		return 0;
  	}
  	double coords[] = new double[6];
  	if (pi.currentSegment(coords) != PathIterator.SEG_MOVETO) {
  		throw new IllegalPathStateException("missing initial moveto "+
  		"in path definition");
  	}
  	pi.next();
  	double movx = coords[0];
  	double movy = coords[1];
  	double curx = movx;
  	double cury = movy;
  	double endx, endy;
  	int crossings = 0;
  	while (!pi.isDone()) {
  		switch (pi.currentSegment(coords)) {
  		case PathIterator.SEG_MOVETO:
  			if (cury != movy) {
  				crossings += pointCrossingsForLine(px, py,
  						curx, cury,
  						movx, movy);
  			}
  			movx = curx = coords[0];
  			movy = cury = coords[1];
  			break;
  		case PathIterator.SEG_LINETO:
  			endx = coords[0];
  			endy = coords[1];
  			crossings += pointCrossingsForLine(px, py,
  					curx, cury,
  					endx, endy);
  			curx = endx;
  			cury = endy;
  			break;
  		case PathIterator.SEG_QUADTO:
  			endx = coords[2];
  			endy = coords[3];
  			crossings += pointCrossingsForQuad(px, py,
  					curx, cury,
  					coords[0], coords[1],
  					endx, endy, 0);
  			curx = endx;
  			cury = endy;
  			break;
  		case PathIterator.SEG_CUBICTO:
  			endx = coords[4];
  			endy = coords[5];
  			crossings += pointCrossingsForCubic(px, py,
  					curx, cury,
  					coords[0], coords[1],
  					coords[2], coords[3],
  					endx, endy, 0);
  			curx = endx;
  			cury = endy;
  			break;
  		case PathIterator.SEG_CLOSE:
  			if (cury != movy) {
  				crossings += pointCrossingsForLine(px, py,
  						curx, cury,
  						movx, movy);
  			}
  			curx = movx;
  			cury = movy;
  			break;
  		}
  		pi.next();
  	}
  	if (cury != movy) {
  		crossings += pointCrossingsForLine(px, py,
  				curx, cury,
  				movx, movy);
  	}
  	return crossings;
  }
  
  /**
   * 
   * @param rect the Rectangle to scale
   * @param scaleX the scale factor in x direction
   * @param scaleY the scale factor in y direction
   * @param onlyDimension determines if only the dimension should be scaled or also the location
   * @return the scaled rectangle
   */
	public static java.awt.Rectangle scaleRect(java.awt.Rectangle rect, double scaleX, double scaleY, boolean onlyDimension)
	{
		int x = rect.x;
		int y = rect.y;
		int width = (int) Math.rint(rect.width * scaleX);
		int height = (int) Math.rint(rect.height * scaleY);
		if (!onlyDimension) {
			x = (int) Math.rint(rect.x * scaleX);
			y = (int) Math.rint(rect.y * scaleY);
		}
		return new java.awt.Rectangle(x, y, width, height);
	}
}
