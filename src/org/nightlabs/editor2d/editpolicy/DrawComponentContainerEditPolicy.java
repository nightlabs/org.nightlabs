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

package org.nightlabs.editor2d.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;


public class DrawComponentContainerEditPolicy 
extends ContainerEditPolicy 
{

  /* (non-Javadoc)
   * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
   */
  protected Command getCreateCommand(CreateRequest request) {
    return null;
  }
  
//  public Command getOrphanChildrenCommand(GroupRequest request) 
//  {
//  	List parts = request.getEditParts();
//  	CompoundCommand result = 
//  		new CompoundCommand(EditorPlugin.getResourceString("command_orphan_children"));
//  	for (int i = 0; i < parts.size(); i++) {
//  		OrphanChildCommand orphan = new OrphanChildCommand();
//  		orphan.setChild((LogicSubpart)((EditPart)parts.get(i)).getModel());
//  		orphan.setParent((LogicDiagram)getHost().getModel());
//  		orphan.setLabel(LogicMessages.LogicElementEditPolicy_OrphanCommandLabelText);
//  		result.add(orphan);
//  	}
//  	return result.unwrap();
//  }  
}
