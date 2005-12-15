/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 28.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.request;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Shape;
import org.eclipse.gef.requests.CreateRequest;

import org.nightlabs.editor2d.j2d.GeneralShape;


public class EditorCreateRequest 
extends CreateRequest 
implements EditorShapeRequest,
					 EditorBoundsRequest
{
  public static final Logger LOGGER = Logger.getLogger(EditorCreateRequest.class);
  
  public static final int BOUNDS_FIX_MODE = 1;
  public static final int BOUNDS_UNFIX_MODE = 2;
  
  protected int mode = BOUNDS_UNFIX_MODE;    
  public int getMode() {
    return mode;
  }
  public void setMode(int mode) {
    this.mode = mode;
  }
  
  protected GeneralShape gp;
  public GeneralShape getGeneralShape() {
    return gp;
  }  
  public void setGeneralShape(GeneralShape gp) {
    this.gp = gp;
  }
  
  protected Shape shape;  
  public Shape getShape() {
    return shape;
  }
  public void setShape(Shape shape) {
    this.shape = shape;
  }  
  
  protected boolean useShape = false;    
  public boolean isUseShape() {
    return useShape;
  }
  public void setUseShape(boolean useShape) {
    this.useShape = useShape;
  }
  
//  protected boolean ignoreSize = false;    
//  public boolean isIgnoreSize() {
//    return ignoreSize;
//  }  
//  public void setIgnoreSize(boolean ignoreSize) {
//    this.ignoreSize = ignoreSize;
//  }
  
  /**
   * 
   */
  public EditorCreateRequest() {
    super();
  }

  /**
   * @param type
   */
  public EditorCreateRequest(Object type) {
    super(type);
  }
  
}
