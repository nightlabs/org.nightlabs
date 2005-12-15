/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 18.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.handle;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import org.nightlabs.editor2d.util.EditorUtil;


public class MultipleCenterLocator 
extends AbstractMultipleLocator 
{

  public MultipleCenterLocator(List editParts) {
    super(editParts);
  }

  protected Point getLocation() 
  {
    return EditorUtil.getCenter(editParts);
  }

}
