/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.request;

import java.awt.geom.AffineTransform;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.requests.SelectionRequest;


public class EditorShearRequest 
extends SelectionRequest 
{
  protected List parts;
  public List getEditParts() {
    return parts;
  }
  public void setEditParts(List list) {
    parts = list;
  }
  
  protected int direction = PositionConstants.NONE;   
  public int getDirection() {
    return direction;
  }
  public void setDirection(int direction) {
    this.direction = direction;
  }
  
  protected AffineTransform affineTransform;    
  public AffineTransform getAffineTransform() {
    return affineTransform;
  }
  public void setAffineTransform(AffineTransform affineTransform) {
    this.affineTransform = affineTransform;
  }
  
  protected Rectangle shearBounds;  
  public Rectangle getShearBounds() {
    return shearBounds;
  }
  public void setShearBounds(Rectangle shearBounds) {
    this.shearBounds = shearBounds;
  }
}
