/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
 **/
package com.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.ui.views.properties.IPropertySource;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.figures.DrawComponentFigure;
import com.nightlabs.editor2d.figures.RendererFigure;
import com.nightlabs.editor2d.model.DrawComponentPropertySource;
import com.nightlabs.editor2d.request.EditorRequestConstants;
import com.nightlabs.editor2d.util.EditorUtil;
import com.nightlabs.editor2d.util.J2DUtil;

public abstract class AbstractDrawComponentEditPart 
extends AbstractGraphicalEditPart
implements EditorRequestConstants
{
  public static final Logger LOGGER = Logger.getLogger(AbstractDrawComponentEditPart.class);
  
  protected IPropertySource propertySource = null;
  
  public AbstractDrawComponentEditPart(DrawComponent drawComponent) 
  {
    setModel(drawComponent);
  }
  
//  /* 
//   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
//   */
//  protected abstract IFigure createFigure();
  protected IFigure createFigure() 
  {
    DrawComponentFigure figure = new DrawComponentFigure();    
    figure.setDrawComponent(getDrawComponent());    
    addRenderer(figure);
    addZoomListener(figure);
    return figure;
  }
  
  protected void addRenderer(DrawComponentFigure figure) 
  {
    // add Renderer
    if (getDrawComponent().getRenderer() != null) {
    	figure.setRenderer(getDrawComponent().getRenderer());  
    }
    else {
      if (getModelRoot() != null) {
        getDrawComponent().setRenderModeManager(getModelRoot().getMultiLayerDrawComponent().getRenderModeManager());
      }
    }  	
  }
  
  public MultiLayerDrawComponentEditPart getModelRoot() 
  {
  	if (getRoot() instanceof MultiLayerDrawComponentEditPart) {
  		return (MultiLayerDrawComponentEditPart) getRoot();
  	}
  	else {
  		for (Iterator it = getRoot().getChildren().iterator(); it.hasNext(); ) {
  			Object o = it.next();
  			if (o instanceof MultiLayerDrawComponentEditPart) {
  				return (MultiLayerDrawComponentEditPart) o;
  			}
  		}
  	}
  	return null;
  }
  
  protected void addZoomListener(DrawComponentFigure figure) 
  {
    ZoomManager zoomManager = EditorUtil.getZoomManager(this);
    if (zoomManager != null) {
      zoomManager.addZoomListener(figure.getZoomListener());
    }  	
  }
  
  protected RendererFigure getRendererFigure() 
  {
    return (RendererFigure) getFigure();
  }
  /* 
   * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
   */
  protected abstract void createEditPolicies();
  
  /* 
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
  
  /* 
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
  
  public DrawComponent getDrawComponent() {
    return (DrawComponent) getModel();
  }
        
  protected void refreshVisuals()
  {
    Rectangle r = new Rectangle(J2DUtil.toDraw2D(getDrawComponent().getBounds()));
    
    ((GraphicalEditPart) getParent()).setLayoutConstraint(
        this,
        getFigure(),
        r);  
    
    if (getFigure() instanceof RendererFigure) {
      getRendererFigure().setRenderer(getDrawComponent().getRenderer());
      getRendererFigure().setDrawComponent(getDrawComponent());      
    }
    
    getFigure().repaint();
    updateLayer(getFigure());
    
//    LOGGER.debug("refreshVisuals!");
  }
  
  public void updateLayer(IFigure figure) 
  {
    LayerEditPart layerEditPart = getLayerEditPart();
    if (layerEditPart != null)
      layerEditPart.getBufferedFreeformLayer().refresh(figure);    	
    
//    LOGGER.debug("Update Layer!");
  }
  
  protected LayerEditPart getLayerEditPart() 
  {    
    EditPart parent = getParent();  
    if (parent == null)
      throw new IllegalStateException("Member parent may not be null for DrawComponent"+this.toString());
    
    if (this instanceof LayerEditPart)
      return (LayerEditPart) this;
    if (this instanceof MultiLayerDrawComponentEditPart)
      return null;
    if (this instanceof RootEditPart)            
      return null;        
    if (parent instanceof LayerEditPart)
      return (LayerEditPart) parent;
    
    while (!(parent instanceof LayerEditPart)) {
      parent = parent.getParent(); 
    }    
    return (LayerEditPart) parent;   
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
		else if (propertyName.equals(DrawComponent.TRANSFORM_CHANGED)) {
			LOGGER.debug(propertyName);
			refreshVisuals();
		}				
	}
	  
  /**
   * Registers this edit part as a listener for change notifications
   * to the specified DrawComponent element.
   * 
   * @param element the DrawComponent element that should be observed
   * for change notifications
   */
  protected void hookIntoDrawComponent(DrawComponent element)
  {
    if (element != null)
      element.addPropertyChangeListener(listener);
  }
  
  /**
   * Removes this edit part from the specified DrawComponent element.
   * Thus, it will no longe receive change notifications.
   * 
   * @param element the DrawComponent element that should not be observed
   * any more
   */
  protected void unhookFromDrawComponent(DrawComponent element)
  {
    if (element != null)
      element.removePropertyChangeListener(listener);
  }
            
  public boolean understandsRequest(Request req) 
  {
    if (req.getType().equals(REQ_ROTATE))
      return true;

    else if (req.getType().equals(REQ_EDIT_ROTATE_CENTER))
      return true;

    else if (req.getType().equals(REQ_SHEAR))
      return true;
    
    return super.understandsRequest(req);
  }  
}
