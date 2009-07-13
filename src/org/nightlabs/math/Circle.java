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


public class Circle
{
  protected double middleX;
  public double getMiddleX() {
    return middleX;
  }
  public void setMiddleX(double middleX) {
    this.middleX = middleX;
  }
  
  protected double middleY;
  public double getMiddleY() {
    return middleY;
  }
  public void setMiddleY(double middleY) {
    this.middleY = middleY;
  }
  
  protected double radius;
  public double getRadius() {
    return radius;
  }
  public void setRadius(double radius) {
    this.radius = radius;
  }

  public Point2D getCenter() {
    return new Point2D.Double(middleX, middleY);
  }
  public void setCenter(Point2D center) {
    middleX = center.getX();
    middleY = center.getY();
  }
    
  public Circle(double middleX, double middleY, double radius)
  {
    super();
    this.middleX = middleX;
    this.middleY = middleY;
    this.radius = radius;
  }

  public Circle(Point2D center, double radius) {
    super();
    middleX = center.getX();
    middleY = center.getY();
    this.radius = radius;
  }
 
  public double getDiamter() {
    return 2 * radius;
  }
}
