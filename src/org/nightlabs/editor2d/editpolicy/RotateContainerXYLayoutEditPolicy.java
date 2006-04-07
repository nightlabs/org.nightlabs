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
import org.nightlabs.editor2d.command.RotateCenterCommand;
import org.nightlabs.editor2d.command.RotateCommand;
import org.nightlabs.editor2d.request.EditorRotateCenterRequest;
import org.nightlabs.editor2d.request.EditorRotateRequest;
import org.nightlabs.editor2d.util.EditorUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class RotateContainerXYLayoutEditPolicy 
extends FeedbackContainerXYLayoutEditPolicy
{

	public RotateContainerXYLayoutEditPolicy() {
		super();
	}

  public Command getCommand(Request request) 
  {    
    if (request instanceof EditorRotateRequest)
      return getRotateCommand((EditorRotateRequest)request);

    if (request instanceof EditorRotateCenterRequest)
      return getRotateCenterCommand((EditorRotateCenterRequest)request);
    
  	return super.getCommand(request);
  }  
	
  protected Command getRotateCenterCommand(EditorRotateCenterRequest request) 
  {
    RotateCenterCommand cmd = new RotateCenterCommand(request);
    Point rotationCenter = request.getRotationCenter().getCopy();
    rotationCenter = EditorUtil.toAbsolute(getHost(), rotationCenter.x, rotationCenter.y);
    cmd.setRotationCenter(rotationCenter);    
    LOGGER.debug("cmd.rotationCenter = "+rotationCenter);
    return cmd;
  }
  
  protected Command getRotateCommand(EditorRotateRequest request) 
  {
    RotateCommand cmd = new RotateCommand(request);
    double rotation = request.getRotation();
    cmd.setRotation(rotation);
    LOGGER.debug("getRotateCommand().rotation = "+rotation);
    return cmd;
  }  
}
