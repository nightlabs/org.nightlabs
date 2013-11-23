/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.math;

import java.awt.geom.Point2D;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Daniel Mazurek - daniel.mazurek at nightlabs dot de
 */
public class MathUtil
{

	protected MathUtil()
	{
	}

	public static double log(double base, double value)
	{
		return Math.log(value) / Math.log(base);
	}
	
	/**
	 * calculates a circle which outline contains all three given parameters
	 * 
	 * @param point1 an arbitrarily Point
	 * @param point2 an arbitrarily Point
	 * @param point3 an arbitrarily Point
	 * @return a Ccrcle which outline contains all three parameters point1, point2, point3
	 */
	public static Circle getCircle(Point2D point1, Point2D point2, Point2D point3)
	{
	  // Alternative Arc2D.setArcByTangent(Point2D p1,Point2D p2,Point2D p3,double radius)
    double a,b,c,d,e,f;
    a=point1.getX();
    b=point1.getY();
    c=point2.getX();
    d=point2.getY();
    e=point3.getX();
    f=point3.getY();
    double middleX = ((Math.pow(a,2)+Math.pow(b,2))*(f-d) + (Math.pow(c,2)+Math.pow(d,2))*(b-f) + (Math.pow(e,2)+Math.pow(f,2))*(d-b)) / (2*(a*(f-d)+c*(b-f)+e*(d-b)));
    double middleY = ((Math.pow(a,2)+Math.pow(b,2))*(e-c) + (Math.pow(c,2)+Math.pow(d,2))*(a-e) + (Math.pow(e,2)+Math.pow(f,2))*(c-a)) / (2*(b*(e-c)+d*(a-e)+f*(c-a)));
    double radius = Math.sqrt(Math.pow(a-middleX,2) + Math.pow(b-middleY,2));
    return new Circle(middleX, middleY, radius);
	}
	
	/**
	 * Calculates the rotation in degrees depending on a certain point (mainly the mousePosition)
	 * and another fix point
	 * 
	 * @param mouseX X-Coordinate of the mouse
	 * @param mouseY Y-Coordinate of the mouse
	 * @param centerX X-Coordinate of a fix point
	 * @param centerY Y-Coordinate of a fix point
	 * @return the rotation (angle) of the line described through two points given by (mouseX, mouseY) and (centerX, centerY)
	 */
  public static double calcRotation(double mouseX, double mouseY, double centerX, double centerY)
  {
    double rotation = 0;
    if (mouseX > centerX && mouseY < centerY) {
      rotation = 360-180 * Math.atan((mouseX-centerX)/(centerY-mouseY))/Math.PI;
    }
    else if (mouseX < centerX && mouseY < centerY) {
      rotation = 180 * Math.atan((centerX-mouseX)/(centerY-mouseY))/Math.PI;
    }
    else if (mouseX < centerX && mouseY > centerY) {
      rotation = 90 + 180 * Math.atan((mouseY-centerY)/(centerX-mouseX))/Math.PI;
    }
    else if (mouseX > centerX && mouseY > centerY) {
      rotation = 180 + 180 * Math.atan((mouseX-centerX)/(mouseY-centerY))/Math.PI;
    }
    return rotation;
  }
        
  /**
   * calculates the Point on the Line which goes through both points (x1,x2) (y1,y2) with the distance d from first Point
   * 
   * @param x1 x-coordinate of first Point
   * @param y1 y-coordinate of first Point
   * @param x2 x-coordinate of second Point
   * @param y2 y-coordinate of second Point
   * @param d the distance from first Point
   * @return the Point on the Line which goes through both points (x1,x2) (y1,y2) with the distance d from first Point
   */
  public static Point2D getPointOnLineWithDistance(double x1, double y1, double x2, double y2, double d)
  {
    double x = 0;
    double y = 0;
    
    if (x1 == x2)
      x = x1;
    else
      x = x1 + (d*(x1-x2) / Math.sqrt( (Math.pow(x1-x2,2)) + (Math.pow(y1-y2,2)) ));
    
    if (y1 == y2)
      y = y1;
    else
//      y = x2 + (d*(y1-y2) / Math.sqrt( (Math.pow(x1-x2,2)) + (Math.pow(y1-y2,2)) ));
    	y = y1 + (d*(y1-y2) / Math.sqrt( (Math.pow(x1-x2,2)) + (Math.pow(y1-y2,2)) ));
    
    return new Point2D.Double(x, y);
  }
  
  /**
   * calculates the Point on the Line which goes through both points (p1) and (p2) with the distance d from p1
   * 
   * @param p1 first Point
   * @param p2 second Point
   * @param d the distance from p1
   * @return @see getPointOnLineWithDistance(double x1, double y1, double x2, double y2, double d)
   */
  public static Point2D getPointOnLineWithDistance(Point2D p1, Point2D p2, double d)
  {
    return getPointOnLineWithDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY(), d);
  }

  /**
   * calculates the distance between two points
   * 
   * @param x1 the X-Coordinate of the first point
   * @param y1 the Y-Coordinate of the first point
   * @param x2 the X-Coordinate of the second point
   * @param y2 the Y-Coordinate of the second point
   * @return the distance between both points
   */
  public static double getDistance(double x1, double y1, double x2, double y2)
  {
  	return Math.sqrt( Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2) );
  }
  
  /**
   * calculates the distance between two points
   * 
   * @param p1 the first Point
   * @param p2 the second Point
   * @return the distance between both points
   */
  public static double getDistance(Point2D p1, Point2D p2)
  {
  	return getDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }
}
