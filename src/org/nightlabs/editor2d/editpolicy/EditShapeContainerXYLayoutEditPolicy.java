/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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
package org.nightlabs.editor2d.editpolicy;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.command.EditShapeCommand;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorEditShapeRequest;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditShapeContainerXYLayoutEditPolicy 
extends FeedbackContainerXYLayoutEditPolicy 
{

	public EditShapeContainerXYLayoutEditPolicy() {
		super();
	}

	public Command getCommand(Request request) 
  {    
  	if (REQ_EDIT_SHAPE.equals(request.getType()))
  		return getEditShapeCommand((EditorEditShapeRequest)request);
    
  	return super.getCommand(request);
  }  
	
  /**
   * Returns the command contribution for the given edit shape request. 
   * By default, the request is redispatched to the host's parent as a {@link
   * org.nightlabs.editor2d.request.EditorRequestConstants#REQ_EDIT_SHAPE}.  
   * The parent's editpolicies determine how to perform the resize based on the layout manager in use.
   * @param request the edit shape request
   * @return the command contribution obtained from the parent
   */
  protected Command getEditShapeCommand(EditorEditShapeRequest request) 
  {
    EditShapeCommand editShapeCommand = null;
    if (editShapeCommand == null) 
    {
    	editShapeCommand = new EditShapeCommand();
    	ShapeDrawComponentEditPart sdcEP = (ShapeDrawComponentEditPart) request.getTargetEditPart();
    	ShapeDrawComponent sdc = sdcEP.getShapeDrawComponent();
    	editShapeCommand.setShapeDrawComponent(sdc);
    	editShapeCommand.setPathSegmentIndex(request.getPathSegmentIndex());
    	editShapeCommand.setLabel(EditorPlugin.getResourceString("command.edit.shape"));      
    }
  	Point modelPoint = getConstraintPointFor(request.getLocation());
  	editShapeCommand.setLocation(modelPoint); 
		return editShapeCommand;		
  } 	
}
