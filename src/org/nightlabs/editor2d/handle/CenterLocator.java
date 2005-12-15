/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.handle;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalEditPart;


public class CenterLocator 
extends AbstractLocator 
{  
  public CenterLocator(GraphicalEditPart owner) 
  {
    super(owner);
  }

  protected Point getLocation() {
    return figure.getBounds().getCenter();
  } 
      
}
