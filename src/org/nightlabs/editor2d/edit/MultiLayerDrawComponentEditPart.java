/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.edit;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.ui.views.properties.IPropertySource;

import org.nightlabs.editor2d.MultiLayerDrawComponent;
import org.nightlabs.editor2d.editpolicy.MultiLayerDrawComponentXYLayoutPolicy;
import org.nightlabs.editor2d.model.LayerPropertySource;
import org.nightlabs.editor2d.model.MultiLayerDrawComponentPropertySource;

public class MultiLayerDrawComponentEditPart 
extends AbstractDrawComponentContainerEditPart
{  
  public static final Logger LOGGER = Logger.getLogger(MultiLayerDrawComponentEditPart.class);

  public static UpdateManager updateManager = null;
  
	public MultiLayerDrawComponentEditPart(MultiLayerDrawComponent mldc) {
	  super(mldc);
	  setModel(mldc);	  
	}
  
	/* 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure()
	{
//	  SmartUpdateFreeformLayer f = new SmartUpdateFreeformLayer(this);
//	  f.setMldcEditPart(this);
//	  EditPartViewer viewer = getRoot().getViewer();
//	  if (viewer instanceof J2DScrollingGraphicalViewer) {
//	    J2DScrollingGraphicalViewer j2DViewer = (J2DScrollingGraphicalViewer) viewer;
//	    FigureCanvas figureCanvas = (FigureCanvas)j2DViewer.getControl();	    
//	    updateManager = figureCanvas.getLightweightSystem().getUpdateManager();
//	    f.registerOnDeferredUpdateManager(updateManager);
//	  }
//	  IFigure f = new MLDCFreeformLayer();
//	  Figure f = new OversizedBufferFreeformLayer(this);
		Figure f = new FreeformLayer();
//	  RendererFigure f = new org.nightlabs.editor2d.figures.FreeformLayer();	  
		f.setLayoutManager(new FreeformLayout());				
		return f; 		
	}

	/* 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies()
	{
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());		
		// handles constraint changes (e.g. moving and/or resizing) of model elements
		// and creation of new model elements
		XYLayout layout = (XYLayout) getContentPane().getLayoutManager();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new MultiLayerDrawComponentXYLayoutPolicy(layout));		
		// disable selection feedback for this edit part
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);	
		installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$		
	}

	public MultiLayerDrawComponent getMultiLayerDrawComponent() {
		return (MultiLayerDrawComponent) getModel();
	}
		
  /** 
   * @return a List of all Layers
   */
  protected List getModelChildren()
  {  
    return getMultiLayerDrawComponent().getDrawComponents();
  }	

//  protected List getModelChildren()
//  {  
//    List allDrawComponents = new ArrayList(); 
//    for (Iterator itLayers = getMultiLayerDrawComponent().getDrawComponents().iterator(); itLayers.hasNext(); ) {
//      Layer l = (Layer) itLayers.next();
//      l.eAdapters().add(this);
//      if (l.isVisible()) {
//        for (Iterator itDrawOrder = l.getDrawComponents().iterator(); itDrawOrder.hasNext(); ) {
//          DrawComponent dc = (DrawComponent) itDrawOrder.next();
//          allDrawComponents.add(dc);        
//        }        
//      }
//    }
//    return allDrawComponents;
//  }	
	
  /**
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  public Object getAdapter(Class adapter) 
  {    
  	if (adapter == SnapToHelper.class) {
  		List snapStrategies = new ArrayList();
  		Boolean val = (Boolean)getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
  		if (val != null && val.booleanValue())
  			snapStrategies.add(new SnapToGuides(this));
  		val = (Boolean)getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
  		if (val != null && val.booleanValue())
  			snapStrategies.add(new SnapToGeometry(this));
  		val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
  		if (val != null && val.booleanValue())
  			snapStrategies.add(new SnapToGrid(this));
  		
  		if (snapStrategies.size() == 0)
  			return null;
  		if (snapStrategies.size() == 1)
  			return (SnapToHelper)snapStrategies.get(0);

  		SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
  		for (int i = 0; i < snapStrategies.size(); i++)
  			ss[i] = (SnapToHelper)snapStrategies.get(i);
  		return new CompoundSnapToHelper(ss);
  	}
  	return super.getAdapter(adapter);
  }  
  
  /* (non-Javadoc)
   * @see com.ibm.itso.sal330r.gefdemo.edit.WorkflowElementEditPart#getPropertySource()
   */
  protected IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new MultiLayerDrawComponentPropertySource(getMultiLayerDrawComponent());
    }
    return propertySource;
  }   
  
//  public DragTracker getDragTracker(Request req){
//  	if (req instanceof SelectionRequest 
//  		&& ((SelectionRequest)req).getLastButtonPressed() == 3)
//  			return new DeselectAllTracker(this);
//  	return new MarqueeDragTracker();
//  }  
}
