/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 08.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit.tree;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.EllipseDrawComponent;
import com.nightlabs.editor2d.model.EllipsePropertySource;


public class EllipseTreeEditPart 
extends DrawComponentTreeEditPart 
{
//  public static Image ELLIPSE_ICON = ImageDescriptor.createFromURL(EditorPlugin.getDefault().getBundle().getEntry(EditorPlugin.getResourceString("icon_ellipse"))).createImage();
  public static Image ELLIPSE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class, "icons/ellipse16.gif").createImage();
  
  /**
   * @param model
   */
  public EllipseTreeEditPart(EllipseDrawComponent model) {
    super(model);
  }

  /* (non-Javadoc)
   * @see com.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart#getIcon()
   */
  public Image getImage() {
    return ELLIPSE_ICON;
  }

  protected IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new EllipsePropertySource(getEllipseDrawComponent());
    }
    return propertySource;
  }  
  
  public EllipseDrawComponent getEllipseDrawComponent() {
  	return (EllipseDrawComponent) getDrawComponent();
  }
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(EllipseDrawComponent.PROP_END_ANGLE)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(EllipseDrawComponent.PROP_START_ANGLE)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}		
	}  
}
