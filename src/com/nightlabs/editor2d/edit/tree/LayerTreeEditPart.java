/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 13.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit.tree;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.Layer;


public class LayerTreeEditPart 
extends DrawComponentContainerTreeEditPart 
{
  public static Image LAYER_ICON = ImageDescriptor.createFromFile(EditorPlugin.class, "icons/layers16.gif").createImage();
  
  /**
   * @param drawComponent
   */
  public LayerTreeEditPart(Layer layer) {
    super(layer);
  }

  /* (non-Javadoc)
   * @see com.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart#getIcon()
   */
  public Image getImage() {
    return LAYER_ICON;
  }  
}
