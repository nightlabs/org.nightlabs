/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.figures;

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
