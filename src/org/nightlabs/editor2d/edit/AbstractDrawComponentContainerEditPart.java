/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 28.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;

import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.editpolicy.DrawComponentContainerEditPolicy;
import org.nightlabs.editor2d.editpolicy.DrawComponentContainerXYLayoutPolicy;
import org.nightlabs.editor2d.editpolicy.DrawComponentEditPolicy;
import org.nightlabs.editor2d.figures.ContainerDrawComponentFigure;


public abstract class AbstractDrawComponentContainerEditPart 
extends AbstractDrawComponentEditPart 
{
  public static final Logger LOGGER = Logger.getLogger(AbstractDrawComponentContainerEditPart.class);
  
  /**
   * @param drawComponent
   */
  public AbstractDrawComponentContainerEditPart(DrawComponentContainer drawComponentContainer) {
    super(drawComponentContainer);
  }

//  protected abstract IFigure createFigure();
  protected IFigure createFigure() 
  {
    ContainerDrawComponentFigure figure = new ContainerDrawComponentFigure();
    figure.setLayoutManager(new FreeformLayout());    
    figure.setDrawComponent(getDrawComponent());    
    addRenderer(figure);
    addZoomListener(figure);
    return figure;  	
  }
  
//  /* (non-Javadoc)
//   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
//   */
//  protected IFigure createFigure() {
//		Figure f = new Figure();
//		f.setLayoutManager(new FreeformLayout());
//		return f;
//  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
   */
  protected void createEditPolicies() 
  {
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new DrawComponentEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new DrawComponentContainerEditPolicy());
		XYLayout layout = (XYLayout) getContentPane().getLayoutManager();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DrawComponentContainerXYLayoutPolicy(layout));
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);	
		installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$				
  }  
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(DrawComponentContainer.CHILD_ADDED)) {
			LOGGER.debug(propertyName);
			refreshChildren();			
		}
		else if (propertyName.equals(DrawComponentContainer.CHILD_REMOVED)) {
			LOGGER.debug(propertyName);
			refreshChildren();			
		}		
	}

//	public void notifyChanged(Notification notification) 
//	{
//    int type = notification.getEventType();
//    int featureId = notification.getFeatureID(Editor2DPackage.class);
//     
//    if (featureId == Editor2DPackage.DRAW_COMPONENT_CONTAINER__DRAW_COMPONENTS)
//    {
//      switch (type)
//      {
//        case Notification.ADD :
//          LOGGER.debug("DRAW_COMPONENT_CONTAINER__DRAW_COMPONENTS ADD Notified!");
//          refreshChildren();
//          break;
//        case Notification.REMOVE :
//          LOGGER.debug("DRAW_COMPONENT_CONTAINER__DRAW_COMPONENTS REMOVE Notified!");
//          refreshChildren();
//          break;
//        case Notification.ADD_MANY :
//          LOGGER.debug("DRAW_COMPONENT_CONTAINER__DRAW_COMPONENTS ADD MANY Notified!");
//          refreshChildren();
//          break;                            
//        case Notification.REMOVE_MANY :
//          LOGGER.debug("DRAW_COMPONENT_CONTAINER__DRAW_COMPONENTS REMOVE MANY Notified!");
//          refreshChildren();
//          break;                            
//        case Notification.SET :
//          LOGGER.debug("DRAW_COMPONENT_CONTAINER__DRAW_COMPONENTS SET Notified!");
//          refreshChildren();
//          break;                                      
//      }      
//    }            
//    if (type == Notification.SET) {
//      LOGGER.debug("DrawComponentContainer SET Notified!");       
//      refreshVisuals();
//    }
//        
//		super.notifyChanged( notification );
//	}
	
  protected List getModelChildren()
  {
    return ((DrawComponentContainer)getModel()).getDrawComponents();
  }   
}
