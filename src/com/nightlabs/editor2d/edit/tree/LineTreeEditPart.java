/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 22.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit.tree;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.LineDrawComponent;


public class LineTreeEditPart 
extends DrawComponentTreeEditPart 
{
  public static Image LINE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class, "icons/line16.gif").createImage();
  
  /**
   * @param drawComponent
   */
  public LineTreeEditPart(DrawComponent drawComponent) {
    super(drawComponent);

  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
   */
  protected Image getImage() {
    return LINE_ICON;
  }

//	protected void propertyChanged(PropertyChangeEvent evt) 
//	{
//		super.propertyChanged(evt);
//		String propertyName = evt.getPropertyName();
//		if (propertyName.equals(LineDrawComponent.PROP_CONNECT)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}
//	}  
}
