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

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertySource;

import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.model.ImagePropertySource;

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
  
  public IPropertySource getPropertySource()
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
