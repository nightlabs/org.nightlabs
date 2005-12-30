/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.ui.views.properties.IPropertySource;

import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.model.TextPropertySource;

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
