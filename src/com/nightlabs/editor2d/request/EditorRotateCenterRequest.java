/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.request;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.SelectionRequest;


public class EditorRotateCenterRequest 
extends SelectionRequest 
{
  protected Point rotationCenter;  
  public Point getRotationCenter() {
    return rotationCenter;
  }
  public void setRotationCenter(Point rotationCenter) {
    this.rotationCenter = rotationCenter;
  }
  
  protected List parts;
  public List getEditParts() {
    return parts;
  }
  public void setEditParts(List list) {
    parts = list;
  }  
  
  protected boolean multiple = false;  
  public boolean isMultiple() {
    return multiple;
  }
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }
}
