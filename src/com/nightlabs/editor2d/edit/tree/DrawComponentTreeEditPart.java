/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 12.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.edit.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.editpolicy.DrawComponentEditPolicy;
import com.nightlabs.editor2d.editpolicy.tree.DrawComponentTreeEditPolicy;
import com.nightlabs.editor2d.model.DrawComponentPropertySource;


public abstract class DrawComponentTreeEditPart 
extends AbstractTreeEditPart 
{
  public static final Logger LOGGER = Logger.getLogger(DrawComponentTreeEditPart.class);  
  protected IPropertySource propertySource = null;
    
  /**
   * Creates a new DrawComponentTreeEditPart instance.
   * @param model
   */
  public DrawComponentTreeEditPart(DrawComponent drawComponent) {
    super(drawComponent);
  } 
  
  /* (non-Javadoc)
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
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
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
   */
  protected abstract Image getImage();
  
  /* (non-Javadoc)
   * @see com.ibm.itso.sal330r.gefdemo.edit.WorkflowElementEditPart#getPropertySource()
   */
  protected IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new DrawComponentPropertySource(getDrawComponent());
    }
    return propertySource;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
   */
  protected String getText()
  {
  	return getDrawComponent().getName();
//    // TODO Default Tree Name should come from somewhere else
//    // respectively the default name should already be set at this point
//    return 
//    (null != getDrawComponent().getName()
//        ? getDrawComponent().getName()
//        : EditorPlugin.getResourceString("outline_unnamed"));
  }

  /**
   * Returns the model as <code>DrawComponent</code>.
   * @return the model as <code>DrawComponent</code>
   */
  public DrawComponent getDrawComponent() {
    return (DrawComponent) getModel();
  }  
    

  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
   */
  public void activate()
  {
    if (isActive())
        return;

    // start listening for changes in the model
    hookIntoDrawComponent(getDrawComponent());

    super.activate();
  }


  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
   */
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
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}
		else if (propertyName.equals(DrawComponent.PROP_HEIGHT)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}
		else if (propertyName.equals(DrawComponent.PROP_WIDTH)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}
		else if (propertyName.equals(DrawComponent.PROP_X)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}
		else if (propertyName.equals(DrawComponent.PROP_Y)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION_X)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION_Y)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}						
		else if (propertyName.equals(DrawComponent.PROP_RENDER_MODE)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}		
		else if (propertyName.equals(DrawComponent.PROP_NAME)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}
		else if (propertyName.equals(DrawComponent.PROP_LANGUAGE_ID)) {
			LOGGER.debug(propertyName+"changed!");
			refreshVisuals();
		}			
		
	}  
	
///* (non-Javadoc)
//* @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
//*/
//public void notifyChanged(Notification notification)
//{
// int type = notification.getEventType();
// 
// switch (type)
// {
//   case Notification.ADD :
//   case Notification.ADD_MANY :
//   case Notification.REMOVE :
//   case Notification.REMOVE_MANY :
//     // TODO: need more info to know if children need refresh?
////     LOGGER.debug("refreshChildren!");
//     refreshChildren();
//     break;
//
//   case Notification.SET :
////   	LOGGER.debug("refreshVisuals!");        
//     refreshVisuals();	
//     break;
// }
//}	
}
