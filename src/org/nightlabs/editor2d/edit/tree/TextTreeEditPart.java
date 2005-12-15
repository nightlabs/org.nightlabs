/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.edit.tree;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.model.TextPropertySource;

public class TextTreeEditPart 
extends DrawComponentTreeEditPart 
{
  public static final Image TEXT_ICON = ImageDescriptor.createFromFile(EditorPlugin.class, "icons/text16.gif").createImage();
  
  public TextTreeEditPart(TextDrawComponent drawComponent) {
    super(drawComponent);
  }

  protected Image getImage() {
    return TEXT_ICON;
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
//		else if (propertyName.equals(TextDrawComponent.PROP_FONT_NAME)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}
//		else if (propertyName.equals(TextDrawComponent.PROP_FONT_SIZE)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}
		else if (propertyName.equals(TextDrawComponent.PROP_TEXT)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
//		else if (propertyName.equals(TextDrawComponent.PROP_BOLD)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}
//		else if (propertyName.equals(TextDrawComponent.PROP_ITALIC)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}		
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
}
