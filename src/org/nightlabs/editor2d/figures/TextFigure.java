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
