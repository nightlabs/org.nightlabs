/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 15.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.figures;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import org.nightlabs.editor2d.j2d.GeneralShape;

public class TextFigure 
extends AbstractShapeFigure
{
  public TextFigure() 
  {
    super();
  }

//  public boolean containsPoint(int x, int y) 
//  {
//    return super.containsPoint(x, y);
//  }

//  protected void fillShape(Graphics graphics) 
//  {
//    if (graphics instanceof J2DGraphics) 
//    {
//      j2d = (J2DGraphics) graphics;
//      g2d = j2d.createGraphics2D();
//      g2d.setClip(null);
//      g2d.setPaint(J2DUtil.toAWTColor(getBackgroundColor()));
//      g2d.fill(getGeneralShape());      
//      g2d.dispose();   
//    }     
//  }
  
//  protected void outlineShape(Graphics graphics) 
//  {
//    if (graphics instanceof J2DGraphics) 
//    {
//      j2d = (J2DGraphics) graphics;
//      Graphics2D g2d = j2d.createGraphics2D();
//      fontRenderContext = g2d.getFontRenderContext();
//      textLayout.draw(g2d, getLocation().x, getLocation().y);     
//    }         
//  }
  
  protected FontRenderContext fontRenderContext;
  public FontRenderContext getFontRenderContext() 
  {
    if (fontRenderContext == null)
      fontRenderContext = new FontRenderContext(null, false, false);
    
    return fontRenderContext;
  }
 
  protected TextLayout textLayout;
  protected void setTextlayout(String text, Font font) 
  {
    at.setToIdentity();
    this.textLayout = new TextLayout(text, font, getFontRenderContext());
    setGeneralShape(new GeneralShape(textLayout.getOutline(at)));
  }
    
  protected String text = "text";
  public void setText(String text) 
  {
    this.text = text;
    setTextlayout(text, font);
  }
  
  protected Font font;
  public void setAWTFont(Font font) {
    
    this.font = font;
    setTextlayout(text, font);
  }

}
