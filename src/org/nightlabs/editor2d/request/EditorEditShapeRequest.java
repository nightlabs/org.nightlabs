/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.01.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.request;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.SelectionRequest;


public class EditorEditShapeRequest  
extends SelectionRequest
implements EditorRequestConstants,
					 EditorLocationRequest
{
  protected int pathSegmentIndex;  
  public int getPathSegmentIndex() {
    return pathSegmentIndex;
  }
  public void setPathSegmentIndex(int pathSegmentIndex) {
    this.pathSegmentIndex = pathSegmentIndex;
  }
    
//  protected Point mouseLocation;
//  /**
//   * Returns the location of the mouse pointer.
//   *
//   * @return The location of the mouse pointer.
//   */
//  public Point getLocation() {
//  	return mouseLocation;
//  }
//  
//  /**
//   * Sets the location where the New PathSegment will be placed.
//   *
//   * @param location the location
//   */
//  public void setLocation(Point location) {
//  	this.mouseLocation = location;
//  }
  
  protected EditPart targetEditPart;  
  public EditPart getTargetEditPart() {
    return targetEditPart;
  }
  public void setTargetEditPart(EditPart targetEditPart) {
    this.targetEditPart = targetEditPart;
  }
    
  public EditorEditShapeRequest() 
  {
    super();
    setType(REQ_EDIT_SHAPE);
  }

  /**
   * @param type
   */
  public EditorEditShapeRequest(Object type) 
  {
    super();
    setType(type);
  }

}
