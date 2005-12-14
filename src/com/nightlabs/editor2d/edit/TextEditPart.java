/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.ui.views.properties.IPropertySource;

import com.nightlabs.editor2d.TextDrawComponent;
import com.nightlabs.editor2d.model.TextPropertySource;

public class TextEditPart 
extends ShapeDrawComponentEditPart 
{
  public TextEditPart(TextDrawComponent text) 
  {
    super(text);
  }

  public TextDrawComponent getTextDrawComponent() {
    return (TextDrawComponent) getModel();
  }
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(TextDrawComponent.PROP_FONT)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(TextDrawComponent.PROP_FONT_NAME)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(TextDrawComponent.PROP_FONT_SIZE)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(TextDrawComponent.PROP_TEXT)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(TextDrawComponent.PROP_BOLD)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
		else if (propertyName.equals(TextDrawComponent.PROP_ITALIC)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}		
	}
	
  /* (non-Javadoc)
   * @see com.ibm.itso.sal330r.gefdemo.edit.WorkflowElementEditPart#getPropertySource()
   */
  protected IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new TextPropertySource(getTextDrawComponent());
    }
    return propertySource;
  } 
  
//  public void notifyChanged(Notification notification)
//  {
//    int type = notification.getEventType();
//    int featureId = notification.getFeatureID(Editor2DPackage.class);
//   
//    if (type == Notification.SET)
//    {
//      switch (featureId)
//      {
//        case Editor2DPackage.TEXT_DRAW_COMPONENT__ANTI_ALIASING : 
//          LOGGER.debug("TEXT_DRAW_COMPONENT__ANTI_ALIASING Notified!");         
//          refreshVisuals();
//          break;
//        case Editor2DPackage.TEXT_DRAW_COMPONENT__FONT : 
//          LOGGER.debug("TEXT_DRAW_COMPONENT__FONT Notified!");         
//          refreshVisuals();
//          break;              
//        case Editor2DPackage.TEXT_DRAW_COMPONENT__TEXT : 
//          LOGGER.debug("TEXT_DRAW_COMPONENT__TEXT Notified!");         
//          refreshVisuals();
//          break;
//        case Editor2DPackage.TEXT_DRAW_COMPONENT__BOLD : 
//          LOGGER.debug("TEXT_DRAW_COMPONENT__BOLD Notified!");         
//          refreshVisuals();
//          break;                        
//        case Editor2DPackage.TEXT_DRAW_COMPONENT__ITALIC : 
//          LOGGER.debug("TEXT_DRAW_COMPONENT__ITALIC Notified!");         
//          refreshVisuals();
//          break;                        
//        case Editor2DPackage.TEXT_DRAW_COMPONENT__FONT_SIZE : 
//          LOGGER.debug("TEXT_DRAW_COMPONENT__FONT_SIZE Notified!");         
//          refreshVisuals();
//          break;   
//        case Editor2DPackage.TEXT_DRAW_COMPONENT__FONT_NAME : 
//          LOGGER.debug("TEXT_DRAW_COMPONENT__FONT_NAME Notified!");         
//          refreshVisuals();
//          break;           
//      }
//    }
//    super.notifyChanged(notification);
//  }

//  protected IFigure createFigure() 
//  {
//    TextFigure textFigure = new TextFigure();
//    textFigure.setAWTFont(getTextDrawComponent().getFont());
//    textFigure.setText(getTextDrawComponent().getText());
//    return textFigure; 
//  }
//        
//  protected void refreshVisuals() 
//  {
//    super.refreshVisuals();
//    setTextProperties();
//  }
//
//  protected void setTextProperties() 
//  {
//    getTextFigure().setAWTFont(getTextDrawComponent().getFont());
//    getTextFigure().setText(getTextDrawComponent().getText());    
//  }
//  
//  protected TextFigure getTextFigure() 
//  {
//    return (TextFigure) getFigure();
//  }
 
}
