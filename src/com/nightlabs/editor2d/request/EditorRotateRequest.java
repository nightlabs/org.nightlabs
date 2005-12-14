/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 16.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.request;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.SelectionRequest;

public class EditorRotateRequest 
//extends GroupRequest
extends SelectionRequest
implements EditorRequestConstants,
           EditorLocationRequest,
           EditorGroupRequest
{  
  public EditorRotateRequest() {
    super();
  }
  
  protected Point rotationCenter;  
  public Point getRotationCenter() {
    return rotationCenter;
  }
  public void setRotationCenter(Point rotationCenter) {
    this.rotationCenter = rotationCenter;
  }
  
  protected boolean constrainedRotation = false;
  public boolean isConstrainedRotation() {
    return constrainedRotation;
  }
  public void setConstrainedRotation(boolean constrainedRotation) {
    this.constrainedRotation = constrainedRotation;
  }
  
  protected double rotation = 0;
  public double getRotation() {
    return rotation;
  }
  public void setRotation(double rotation) {
    this.rotation = rotation;
  }
  
  protected List parts;
  public List getEditParts() {
    return parts;
  }
  public void setEditParts(List list) {
    parts = list;
  }  
  
  protected int direction;  
  public int getDirection() {
    return direction;
  }
  public void setDirection(int direction) {
    this.direction = direction;
  }
  
  protected boolean multiple = false;  
  public boolean isMultiple() {
    return multiple;
  }
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }
}
