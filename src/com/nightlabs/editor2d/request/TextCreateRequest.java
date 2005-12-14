/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 10.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.request;


public class TextCreateRequest 
extends EditorCreateRequest 
{  
  protected String fontName;
  public String getFontName() {
    return fontName;
  }  
  public void setFontName(String fontName) {
    this.fontName = fontName;
  }
  
  protected String text;  
  public String getText() {
    return text;
  }  
  public void setText(String text) {
    this.text = text;
  }
  
  protected int fontSize;  
  public int getFontSize() {
    return fontSize;
  }  
  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }
  
  protected int fontStyle;  
  public int getFontStyle() {
    return fontStyle;
  }  
  public void setFontStyle(int fontStyle) {
    this.fontStyle = fontStyle;
  }
  
  public TextCreateRequest() {
    super();
  }

  public TextCreateRequest(Object type) {
    super(type);
  }
  
}
