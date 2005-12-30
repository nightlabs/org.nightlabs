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

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.J2DGraphics;
import org.eclipse.draw2d.geometry.Rectangle;

public class ImageFigure 
extends Figure 
{
  public static final Logger LOGGER = Logger.getLogger(ImageFigure.class);
  
  public ImageFigure(BufferedImage image) 
  {
    super();
    this.bufferedImage = image;
  }
  
  protected AffineTransform at = new AffineTransform();
  
  protected BufferedImage bufferedImage;
  public BufferedImage getBufferedImage() {
    return bufferedImage;
  }
  public void setBufferedImage(BufferedImage image) {
    this.bufferedImage = image;
    bounds = null;
  }  
    
  protected J2DGraphics j2d;  
  protected void paintFigure(Graphics graphics) 
  {
    if (graphics instanceof J2DGraphics) 
    {
      j2d = (J2DGraphics) graphics;
      if (bufferedImage != null) {
//        j2d.drawImage(bufferedImage, getBounds().x, getBounds().y, bufferedImage.getWidth(), bufferedImage.getHeight(), null);        
//        j2d.drawImage(bufferedImage, getImageBounds().x, getImageBounds().y, bufferedImage.getWidth(), bufferedImage.getHeight(), null);        
//        j2d.drawImage(bufferedImage, at, null);
      }
      else {
        // TODO: add noImage Image as default
        j2d.drawString("NoImage", getBounds().getCenter());        
      }
    }
  }
  
  // TODO: Maybe rename rectangle bounds, because it overrides Figure.bounds
  protected Rectangle bounds;
  public Rectangle getBounds() 
  {
    if (bounds == null) 
    {
      if (bufferedImage != null)
        bounds = new Rectangle(super.getBounds().x, super.getBounds().y, bufferedImage.getWidth(), bufferedImage.getHeight());
      else
        return super.getBounds();
    }
    return bounds;      
  }
  
  public void setBounds(Rectangle rect) 
  {        
    super.setBounds(rect);
    bounds = null;    
  }
    
//  public void transform(AffineTransform at) 
//  {
//    AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//    bufferedImage = op.filter(bufferedImage, null);
//  }
//  
//  public void performScale(double factor) 
//  { 
//    AffineTransform transform = new AffineTransform();
//    transform.scale(factor, factor);
//    transform(transform);
//  }
//  
//  public void performTranslate(int dx, int dy) 
//  {            
//    AffineTransform transform = new AffineTransform();
//    transform.translate(dx, dy);
//    transform(transform);
//  }
}
