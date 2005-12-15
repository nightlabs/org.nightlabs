/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 09.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.request;

import org.nightlabs.editor2d.j2d.GeneralShape;


public interface EditorShapeRequest 
{
  public GeneralShape getGeneralShape();
  public void setGeneralShape(GeneralShape generalShape);
}
