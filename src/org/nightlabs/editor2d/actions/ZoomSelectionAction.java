/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 06.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.actions;

import java.util.Iterator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.custom.EditorImages;
import org.nightlabs.editor2d.util.EditorUtil;


public class ZoomSelectionAction 
extends SelectionAction 
{
  public static final String ID = ZoomSelectionAction.class.getName();
  
  protected final Rectangle EMPTY_RECTANGLE = new Rectangle();  
  /**
   * @param part
   */
  public ZoomSelectionAction(IWorkbenchPart part) {
    super(part); 
  }

  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.zoom.selection.label"));
  	setToolTipText(EditorPlugin.getResourceString("action.zoom.selection.tooltip"));
  	setId(ID);
  	setImageDescriptor(EditorImages.ZOOM_SELECTION_16);  	
  } 
      
  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
   */
  protected boolean calculateEnabled() 
  {
    if (getSelectedObjects() != null && !getSelectedObjects().isEmpty()) 
    {
    	for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) {
    		Object o = it.next();
    		if (o instanceof GraphicalEditPart) {
    			GraphicalEditPart editPart = (GraphicalEditPart) o;
    			IFigure f = editPart.getFigure();
    			if (!f.getBounds().equals(EMPTY_RECTANGLE))
    				return true;
    		}
    	}
    }        
    return false;
  }  
  
  public void run() 
  {
    Rectangle totalBounds = null;
    GraphicalEditPart editPart = null;
    for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) 
    {
      Object o = it.next();
      if (o instanceof GraphicalEditPart) {
      	editPart = (GraphicalEditPart) o;
        Rectangle bounds = editPart.getFigure().getBounds();
        if (totalBounds == null) {
        	totalBounds = bounds.getCopy();
        }
        totalBounds.union(bounds);
      }
    }
    if (totalBounds != null & editPart != null) {
    	ZoomManager zoomManager = EditorUtil.getZoomManager(editPart);
    	if (zoomManager != null)
      	EditorUtil.zoomToRelativeRect(totalBounds, zoomManager);
    }
  }
    
}
