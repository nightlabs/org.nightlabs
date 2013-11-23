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

import java.awt.Rectangle;
import java.awt.geom.Arc2D;


public class GeneralShapeFactory
{
  public GeneralShapeFactory() {
    super();
  }

  public static GeneralShape createEllipse(int x, int y, int width, int height)
  {
    Arc2D arc = new Arc2D.Double(x, y, width, height, 0, 360, Arc2D.OPEN);
    return new GeneralShape(arc);
  }

  public static GeneralShape createEllipse(Rectangle rect)
  {
    Arc2D arc = new Arc2D.Double(rect.x, rect.y, rect.width, rect.height, 0, 360, Arc2D.OPEN);
    return new GeneralShape(arc);
  }
    
  public static GeneralShape createRectangle(int x, int y, int width, int height)
  {
//    Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
//    return new GeneralShape(rect);
    GeneralShape gs = new GeneralShape();
    gs.moveTo(x, y);
    gs.lineTo(x, y+height);
    gs.lineTo(x+width, y+height);
    gs.lineTo(x+width, y);
    gs.closePath();
    return gs;
  }
  
  public static GeneralShape createRectangle(Rectangle rect)
  {
    return new GeneralShape(rect);
  }
   
}
