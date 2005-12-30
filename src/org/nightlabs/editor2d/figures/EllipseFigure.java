/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.figures;


public class EllipseFigure 
extends AbstractShapeFigure 
{

  public EllipseFigure() 
  {
    super();
//    createableFromBounds = true;
//    getGeneralShape();
  }
    
//  /* (non-Javadoc)
//   * @see org.nightlabs.editor2d.figures.AbstractShapeFigure#getGeneralPath()
//   */
//  public GeneralShape getGeneralShape() 
//  {
//    if (gp == null) {
////      Arc2D arc = new Arc2D.Double(getGPBounds().x, getGPBounds().y, getGPBounds().width, getGPBounds().height, startAngle, endAngle, Arc2D.OPEN);      
//      Arc2D arc = new Arc2D.Double(bounds.x, bounds.y, bounds.width, bounds.height, startAngle, endAngle, Arc2D.OPEN);      
//      gp = new GeneralShape(arc);
//    }       
//    return gp;
//  }
  
  protected double endAngle = 360;  
  public double getEndAngle() {
    return endAngle;
  }
  public void setEndAngle(double endAngle) {
    this.endAngle = endAngle;
  }
  
  protected double startAngle = 0;
  public double getStartAngle() {
    return startAngle;
  }
  public void setStartAngle(double startAngle) {
    this.startAngle = startAngle;
  }
}
