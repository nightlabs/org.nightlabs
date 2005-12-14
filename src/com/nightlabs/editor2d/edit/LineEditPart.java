/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import com.nightlabs.editor2d.LineDrawComponent;


public class LineEditPart 
extends ShapeDrawComponentEditPart 
{
  /**
   * @param drawComponent
   */
  public LineEditPart(LineDrawComponent drawComponent) {
    super(drawComponent);
  }
  	
	public LineDrawComponent getLineDrawComponent() {
	  return (LineDrawComponent) getModel();
	}
	
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(LineDrawComponent.PROP_CONNECT)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
	}
	
//  public void notifyChanged(Notification notification)
//  {
//    int type = notification.getEventType();
//    int featureId = notification.getFeatureID(Editor2DPackage.class);
//   
//    // TODO: add Notification method for setting PathSegments to model
//    if (type == Notification.SET)
//    {
//      switch (featureId)
//      {
//	      case Editor2DPackage.LINE_DRAW_COMPONENT__CONNECT : 
//	        LOGGER.debug("LINE_DRAW_COMPONENT__CONNECT Notified!");	      	
//		      refreshVisuals();
//		      break;              
//      }
//    }
//    super.notifyChanged(notification);
//  }	
}
