/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 06.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.actions;

import org.eclipse.ui.actions.RetargetAction;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.custom.EditorImages;


public class ZoomSelectionRetargetSelection 
extends RetargetAction 
{
  /**
   * Constructor for ZoomInRetargetAction
   */
  public ZoomSelectionRetargetSelection() {
  	super(null, null);
  	setText(EditorPlugin.getResourceString("action.zoom.selection.label"));
  	setId(ZoomSelectionAction.ID);
  	setToolTipText(EditorPlugin.getResourceString("action.zoom.selection.tooltip"));
  	setImageDescriptor(EditorImages.ZOOM_SELECTION_16);
//  	setActionDefinitionId(GEFActionConstants.ZOOM_IN);
  }

}
