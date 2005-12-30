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

package org.nightlabs.editor2d.config;

import java.awt.Color;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;


public class DefaultFeedbackConfigModule 
extends ConfigModule 
{
  
  public DefaultFeedbackConfigModule() {
    super();
  }

  protected Color bgColor;  
  public Color getBgColor() {
    return bgColor;
  }
  public void setBgColor(Color bgColor) {
    this.bgColor = bgColor;
  }
  
  protected Color fgColor;  
  public Color getFgColor() {
    return fgColor;
  }
  public void setFgColor(Color fgColor) {
    this.fgColor = fgColor;
  }
  
  protected boolean xor = true;  
  public boolean isXor() {
    return xor;
  }
  public void setXor(boolean xor) {
    this.xor = xor;
  }
  
  protected boolean fill = true;  
  public boolean isFill() {
    return fill;
  }
  public void setFill(boolean fill) {
    this.fill = fill;
  }
    
  public void init() 
  throws InitException 
  {
    if (bgColor == null)
      bgColor = Color.DARK_GRAY;
    
    if (fgColor == null)
      fgColor = Color.WHITE;    
  }
}
