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

package org.nightlabs.editor2d.j2d;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;


public class PathSegment
{
  public static final int NO_POINT = 0;
  public static final int POINT = 1;
  public static final int CONTROL_POINT_1 = 2;
  public static final int CONTROL_POINT_2 = 3;
  
  /**
   * Represents a PathSegment of a GeneralShape
   * 
   * param type the type of PathSegment (@see java.awt.geom.PathIterator)
   * param index the index of this PathSegment
   * param coords the Coordinates of the PathSegment
   * 
   */
  public PathSegment(int type, int index, float[] coords, GeneralShape parent)
  {
    if (coords.length != 6)
      throw new IllegalArgumentException("Param coords must have length 6!");
    
    setType(type);
    setIndex(index);
    setCoords(coords);
    this.parent = parent;
  }

  protected int type;
  public int getType() {
    return type;
  }
  protected void setType(int type) {
    this.type = type;
  }
  
  protected int index;
  public int getIndex() {
    return index;
  }
  protected void setIndex(int index) {
    this.index = index;
  }
  
  public float[] coords = new float[6];
  public float[] getCoords() {
    return coords;
  }
  protected void setCoords(float[] coords) {
//    this.coords = coords;
    System.arraycopy(coords, 0, this.coords, 0, coords.length);
  }
  
  public GeneralShape parent;
  public GeneralShape getParent() {
    return parent;
  }
  protected void setParent(GeneralShape parent) {
    this.parent = parent;
  }
  
  public boolean isLineSegement() {
    return type == PathIterator.SEG_LINETO ? true : false;
  }
  
  public boolean isQuadSegment() {
    return type == PathIterator.SEG_QUADTO ? true : false;
  }
  
  public boolean isCubicSegment() {
    return type == PathIterator.SEG_CUBICTO ? true : false;
  }
  
  public boolean isStartSegment() {
    return type == PathIterator.SEG_MOVETO ? true : false;
  }
  
  public Point2D getPoint() {
    return new Point2D.Float(coords[0], coords[1]);
  }
  
  public Point2D getFirstControlPoint()
  {
    if (isQuadSegment() || isCubicSegment())
      return new Point2D.Float(coords[2], coords[3]);
    else
      return new Point2D.Float();
  }

  public Point2D getSecondControlPoint()
  {
    if (isCubicSegment())
      return new Point2D.Float(coords[4], coords[5]);
    else
      return new Point2D.Float();
  }
  
  public void setPoint(float x, float y)
  {
    float[] pointCoords = parent.getPointCoords();
    pointCoords[index] = x;
    pointCoords[index+1] = y;
//    coords[0] = x;
//    coords[1] = y;
  }
  
  public void setFirstControlPoint(float x, float y)
  {
    float[] pointCoords = parent.getPointCoords();
    pointCoords[index+2] = x;
    pointCoords[index+3] = y;
//    coords[2] = x;
//    coords[3] = y;
  }
  
  public void setSecondControlPoint(float x, float y)
  {
    float[] pointCoords = parent.getPointCoords();
    pointCoords[index+4] = x;
    pointCoords[index+5] = y;
//    coords[4] = x;
//    coords[5] = y;
  }
  
  public int contains(int x, int y)
  {
    if (isLineSegement())
    {
      if (coords[0] == x && coords[1] == y)
        return POINT;
      
      return NO_POINT;
    }
    else if (isQuadSegment())
    {
      if (coords[0] == x && coords[1] == y)
        return POINT;
      else if (coords[2] == x && coords[3] == y)
        return CONTROL_POINT_1;
        
      return NO_POINT;
    }
    else if (isCubicSegment())
    {
      if (coords[0] == x && coords[1] == y)
        return POINT;
      else if (coords[2] == x && coords[3] == y)
        return CONTROL_POINT_1;
      else if (coords[4] == x && coords[5] == y)
        return CONTROL_POINT_2;
        
      return NO_POINT;
    }
    return NO_POINT;
  }
}
