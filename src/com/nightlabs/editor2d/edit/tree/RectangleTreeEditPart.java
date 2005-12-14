/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 08.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit.tree;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.RectangleDrawComponent;


public class RectangleTreeEditPart 
extends DrawComponentTreeEditPart 
{
//  public static Image RECTANGLE_ICON = ImageDescriptor.createFromURL(EditorPlugin.getDefault().getBundle().getEntry(EditorPlugin.getResourceString("icon_rectangle"))).createImage();
  public static Image RECTANGLE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class, "icons/rectangle16.gif").createImage();  
  
  /**
   * @param model
   */
  public RectangleTreeEditPart(RectangleDrawComponent model) {
    super(model);
  }

  /* (non-Javadoc)
   * @see com.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart#getIcon()
   */
  public Image getImage() {
    return RECTANGLE_ICON;
  }

}
