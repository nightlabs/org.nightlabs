/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 28.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.figures.BufferedFreeformLayer;
import org.nightlabs.editor2d.figures.OversizedBufferFreeformLayer;
import org.nightlabs.editor2d.model.LayerPropertySource;

public class LayerEditPart 
extends AbstractDrawComponentContainerEditPart
{    
  /**
   * @param drawComponent
   */
  public LayerEditPart(Layer layer, MultiLayerDrawComponentEditPart mldcEditPart) {
    super(layer);
  }
    
  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
   */
  protected IFigure createFigure() 
  {    
//    IFigure f = new DummyFreeformLayer();
//    IFigure f = new org.eclipse.draw2d.FreeformLayer();
//    RendererFigure f = new FreeformLayer();       
//  	Figure f = new SmartUpdateFreeformLayer();
  	Figure f = new OversizedBufferFreeformLayer();
    ((BufferedFreeformLayer)f).init(this);
		f.setLayoutManager(new FreeformLayout());		    		
		return f;  
  }
    
  public Layer getLayer() {
    return (Layer) getModel();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
   */
  protected List getModelChildren() 
  {  
    if (getLayer().isVisible()) {
      return getLayer().getDrawComponents();
    } else {
      return new ArrayList();
    }
  }
  
  public BufferedFreeformLayer getBufferedFreeformLayer() {
    return (BufferedFreeformLayer) getFigure();
  }
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(Layer.PROP_VISIBLE)) {
			LOGGER.debug(propertyName +" changed!");
			refreshChildren();			
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
        new LayerPropertySource(getLayer());
    }
    return propertySource;
  } 
  
//	public void notifyChanged(Notification notification) 
//	{
//    int type = notification.getEventType();
//    int featureId = notification.getFeatureID(Editor2DPackage.class);
//    
//    if (type == Notification.SET) 
//    {
//      switch (featureId) 
//      {
//	      case Editor2DPackage.LAYER__VISIBLE :
//	        LOGGER.debug("LAYER__VISIBLE Notified!");
//		      refreshChildren();
//		      break;                	
//      }
//    }		
//		super.notifyChanged( notification );
//	}
}
