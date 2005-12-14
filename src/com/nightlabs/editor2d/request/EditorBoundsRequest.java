/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 09.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.request;

import org.eclipse.draw2d.geometry.Dimension;


public interface EditorBoundsRequest
extends EditorLocationRequest
{
  public Dimension getSize();
  public void setSize(Dimension newSize);
}
