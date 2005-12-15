/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 17.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.request;

import org.eclipse.draw2d.geometry.Rectangle;


public class LineCreateRequest 
extends EditorCreateRequest 
{
  protected Rectangle creationBounds;  
  public Rectangle getCreationBounds() {
    return creationBounds;
  }
  public void setCreationBounds(Rectangle creationBounds) {
    this.creationBounds = creationBounds;
  }
}
