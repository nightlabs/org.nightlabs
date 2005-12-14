/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 06.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.actions;

import org.eclipse.gef.editparts.ZoomManager;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.custom.EditorImages;


public class ZoomAllAction 
extends ZoomAction 
{
  public static final String ID = ZoomAllAction.class.getName();
  
  /**
   * Constructor for ZoomInAction.
   * @param zoomManager the zoom manager
   */
  public ZoomAllAction(ZoomManager zoomManager) {
  	super(EditorPlugin.getResourceString("action.zoom.all.label"), 
  	    EditorImages.ZOOM_ALL_16, zoomManager);  	
  	setToolTipText(EditorPlugin.getResourceString("action.zoom.all.tooltip"));
  	setId(ID);
//  	setActionDefinitionId(GEFActionConstants.ZOOM_IN);
  }

  /**
   * @see org.eclipse.jface.action.IAction#run()
   */
  public void run() {
  	zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
  }

  /**
   * @see org.eclipse.gef.editparts.ZoomListener#zoomChanged(double)
   */
  public void zoomChanged(double zoom) {
    setEnabled(true);
  }

}
