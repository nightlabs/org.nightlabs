/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 09.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.request;

import org.eclipse.draw2d.geometry.Point;


public interface EditorLocationRequest 
{
  public Point getLocation();
  public void setLocation(Point p);
}
