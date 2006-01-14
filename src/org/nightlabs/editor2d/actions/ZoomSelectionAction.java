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

import java.util.Iterator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.resource.SharedImages;
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
//  	setImageDescriptor(EditorImages.ZOOM_SELECTION_16);  	
  	setImageDescriptor(SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), ZoomSelectionAction.class));
  	setActionDefinitionId(ID);  	
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
