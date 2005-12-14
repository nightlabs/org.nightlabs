/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 08.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.ui.views.properties.IPropertySource;

import com.nightlabs.editor2d.EllipseDrawComponent;
import com.nightlabs.editor2d.model.EllipsePropertySource;


public class EllipseEditPart 
extends ShapeDrawComponentEditPart 
{
  /**
   * @param drawComponent
   */
  public EllipseEditPart(EllipseDrawComponent drawComponent) {
    super(drawComponent);
  }

	protected EllipseDrawComponent getEllipseDrawComponent() {
		return (EllipseDrawComponent) getModel();
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
