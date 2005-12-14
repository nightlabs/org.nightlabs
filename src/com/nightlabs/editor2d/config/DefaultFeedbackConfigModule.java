/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 27.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.config;

import java.awt.Color;

import com.nightlabs.config.ConfigModule;
import com.nightlabs.config.InitException;


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
