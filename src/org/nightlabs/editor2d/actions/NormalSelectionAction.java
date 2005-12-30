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

package org.nightlabs.editor2d.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.EditorStateManager;
import org.nightlabs.editor2d.request.EditorRequestConstants;


public class NormalSelectionAction 
extends SelectionAction 
implements EditorRequestConstants
{
  public static final String ID = NormalSelectionAction.class.getName();
  
//  private Request selectionRequest = new EditorEditShapeRequest(REQ_SELECTION);  
//  public Request getSelectionRequest() {
//    return selectionRequest;
//  }
  
  /**
   * @param part
   */
  public NormalSelectionAction(IWorkbenchPart part) {
    super(part);
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
   */
  protected boolean calculateEnabled() 
  {
    if (EditorStateManager.getCurrentState() != EditorStateManager.STATE_NORMAL_SELECTION) {
      return true;
    }
    return false;
//  	if (getSelectedObjects().size() == 1 && (getSelectedObjects().get(0) instanceof ShapeDrawComponentEditPart)) 
//  	{
//  	  ShapeDrawComponentEditPart part = (ShapeDrawComponentEditPart)getSelectedObjects().get(0);
//  		boolean understands = part.understandsRequest(getSelectionRequest()); 
//  		if (understands && EditorStateManager.getCurrentState() == EditorStateManager.STATE_EDIT_SHAPE);
//  			return true;
//  	}
//  	return false;    
  }
  
  /**
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
   */
  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.normalselection.text"));
  	setToolTipText(EditorPlugin.getResourceString("action.normalselection.tooltip"));
  	setId(ID);  	
//  	setImageDescriptor(SharedImages.DESC_SELECTION_TOOL_16);
  }  
    
  public void run() 
  {
//	EditorStateManager.setCurrentState(EditorStateManager.STATE_NORMAL_SELECTION);
  	if (!getSelectedObjects().isEmpty()) {
  		EditorStateManager.setNormalSelectionMode(getSelectedObjects());
  	}
  }
  	
}
