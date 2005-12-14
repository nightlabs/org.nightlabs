/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertySource;

import com.nightlabs.editor2d.ImageDrawComponent;
import com.nightlabs.editor2d.model.ImagePropertySource;

public class ImageEditPart 
//extends AbstractDrawComponentEditPart 
extends DrawComponentEditPart
{
  public static final Logger LOGGER = Logger.getLogger(ImageEditPart.class);
  
  public ImageEditPart(ImageDrawComponent drawComponent) {
    super(drawComponent);
  }

  protected ImageDrawComponent getImageDrawComponent() {
    return (ImageDrawComponent) getModel();
  }
  
  protected IPropertySource getPropertySource()
  {
    if (propertySource == null) 
    {
      propertySource =
        new ImagePropertySource(getImageDrawComponent());
    }
    return propertySource;
  }
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(ImageDrawComponent.PROP_IMAGE)) {
			LOGGER.debug(propertyName +" changed!");
			refreshVisuals();			
		}
	}    
  
//  protected ImageFigure getImageFigure() {
//    return (ImageFigure) getFigure();
//  }
//  
//  protected IFigure createFigure() 
//  {
//    BufferedImage image = getImageDrawComponent().getImage();    
//    ImageFigure imageFigure = new ImageFigure(image);                
//    return imageFigure;
//  }
//    
//  protected void refreshVisuals() 
//  {
//    getImageFigure().setBufferedImage(getImageDrawComponent().getImage());
//    super.refreshVisuals();
//  }
  
}
