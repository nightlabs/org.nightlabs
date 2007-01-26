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

package org.nightlabs.editor2d.edit.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.decorators.DecoratorManager;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.decorators.VisibleDecorator;
import org.nightlabs.editor2d.editpolicy.DrawComponentEditPolicy;
import org.nightlabs.editor2d.editpolicy.tree.DrawComponentTreeEditPolicy;
import org.nightlabs.editor2d.model.DrawComponentPropertySource;


public abstract class DrawComponentTreeEditPart 
extends AbstractTreeEditPart 
{
  protected IPropertySource propertySource = null;
    
  /**
   * Creates a new DrawComponentTreeEditPart instance.
   * @param model
   */
  public DrawComponentTreeEditPart(DrawComponent drawComponent) {
    super(drawComponent);
  } 
  
  public Object getAdapter(Class key)
  {
    /* override the default behavior defined in AbstractEditPart
    *  which would expect the model to be a property sourced. 
    *  instead the editpart can provide a property source
    */
    if (IPropertySource.class == key)
    {
      return getPropertySource();
    }
    return super.getAdapter(key);
  }  
  
//  protected abstract Image getImage();
  protected abstract Image getOutlineImage();
  
  private Image image;
  protected Image getImage() 
  {
  	if (image == null) {
			if (!getDrawComponent().isVisible()) {
				image = getVisibleCompositeImage().createImage(true);				
			} else {
				image = getOutlineImage();
			}
  		image = getOutlineImage();
  	}
  	return image;
  }
  
  public IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new DrawComponentPropertySource(getDrawComponent());
    }
    return propertySource;
  }
  
  protected String getText()
  {
  	return getDrawComponent().getName();
  }

  /**
   * Returns the model as <code>DrawComponent</code>.
   * @return the model as <code>DrawComponent</code>
   */
  public DrawComponent getDrawComponent() {
    return (DrawComponent) getModel();
  }  
    
  public void activate()
  {
    if (isActive())
        return;

    // start listening for changes in the model
    hookIntoDrawComponent(getDrawComponent());

    super.activate();
  }

  public void deactivate()
  {
    if (!isActive())
        return;

    // stop listening for changes in the model
    unhookFromDrawComponent(getDrawComponent());

    super.deactivate();
  }

  /**
   * Registers this edit part as a listener for change notifications
   * to the specified workflow element.
   * 
   * @param element the drawComponent element that should be observed
   * for change notifications
   */
  protected void hookIntoDrawComponent(DrawComponent element)
  {
    if (element != null)
      element.addPropertyChangeListener(listener);
  }

  /**
   * Removes this edit part from the specified drawComponent element.
   * Thus, it will no longe receive change notifications.
   * 
   * @param element the drawComponent element that should not be observed
   * any more
   */
  protected void unhookFromDrawComponent(DrawComponent element)
  {
    if (element != null)
      element.removePropertyChangeListener(listener);
  }
  
  /**
   * Creates and installs pertinent EditPolicies
   * for this.
   */
  protected void createEditPolicies() 
  {
  	installEditPolicy(EditPolicy.COMPONENT_ROLE, new DrawComponentEditPolicy());
//  	installEditPolicy(EditPolicy.COMPONENT_ROLE, new DrawComponentTreeComponentEditPolicy());  	
  	installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new DrawComponentTreeEditPolicy());
  }
  
  protected PropertyChangeListener listener = new PropertyChangeListener(){	
		public void propertyChange(PropertyChangeEvent evt) {
			propertyChanged(evt);
		}	
	};
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		String propertyName = evt.getPropertyName();		
		if (propertyName.equals(DrawComponent.PROP_BOUNDS)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_HEIGHT)) {
			refreshVisuals();
			return;			
		}
		else if (propertyName.equals(DrawComponent.PROP_WIDTH)) {
			refreshVisuals();
			return;			
		}
		else if (propertyName.equals(DrawComponent.PROP_X)) {
			refreshVisuals();
			return;			
		}
		else if (propertyName.equals(DrawComponent.PROP_Y)) {
			refreshVisuals();
			return;			
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION)) {
			refreshVisuals();
			return;			
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION_X)) {
			refreshVisuals();
			return;			
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION_Y)) {
			refreshVisuals();
			return;			
		}						
		else if (propertyName.equals(DrawComponent.PROP_RENDER_MODE)) {
			refreshVisuals();
			return;			
		}		
		else if (propertyName.equals(DrawComponent.PROP_NAME)) {
			refreshVisuals();
			return;			
		}
		else if (propertyName.equals(DrawComponent.PROP_LANGUAGE_ID)) {
			refreshVisuals();
			return;			
		}	
		else if (propertyName.equals(DrawComponent.PROP_VISIBLE)) {
			if (!getDrawComponent().isVisible()) {
				image = getVisibleCompositeImage().createImage(true);				
			} else {
				image = getOutlineImage();
			}			
//			notifyLabelDecorator();	
			refreshVisuals();
			return;
		}	
	}  
	
	private VisibleCompositeImage visibleCompositeImage;
	private VisibleCompositeImage getVisibleCompositeImage() {
		if (visibleCompositeImage == null) {
			visibleCompositeImage = new VisibleCompositeImage(getOutlineImage());
		}
		return visibleCompositeImage;
	}
	
	protected void notifyLabelDecorator() 
	{
		((DecoratorManager)EditorPlugin.getDefault().getWorkbench().getDecoratorManager()).labelProviderChanged(new
				LabelProviderChangedEvent(((DecoratorManager)EditorPlugin.getDefault().getWorkbench().getDecoratorManager()), this));
		VisibleDecorator decorator = VisibleDecorator.getVisibleDecorator();
		if(decorator!=null) {
			decorator.refresh(this); 		
		}
	}
}
