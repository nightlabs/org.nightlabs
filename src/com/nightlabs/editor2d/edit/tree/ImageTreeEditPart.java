/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit.tree;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.EditorPlugin;

public class ImageTreeEditPart 
extends DrawComponentTreeEditPart 
{
  public static final Image IMAGE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class, "icons/image16.gif").createImage();
  
  public ImageTreeEditPart(DrawComponent drawComponent) {
    super(drawComponent);

  }

  protected Image getImage() {
    return IMAGE_ICON;
  }

}
