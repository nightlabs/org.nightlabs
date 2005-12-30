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

package org.nightlabs.editor2d.editpolicy.tree;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.TreeContainerEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.command.CreateDrawComponentCommand;
import org.nightlabs.editor2d.command.DrawComponentReorderCommand;
import org.nightlabs.editor2d.util.J2DUtil;


public class DrawComponentTreeContainerEditPolicy 
extends TreeContainerEditPolicy 
{
  protected Command createCreateCommand(DrawComponent child, Rectangle r, int index, String label)
  {
    CreateDrawComponentCommand cmd = new CreateDrawComponentCommand();
		Rectangle rect;
		if(r == null) {
			rect = new Rectangle();
			rect.setSize(new Dimension(-1,-1));
		} 
		else {
		  rect = r;
		}
		cmd.setLocation(rect);
		cmd.setParent((DrawComponentContainer)getHost().getModel());
		cmd.setChild(child);
		cmd.setLabel(label);
		if(index >= 0)
		  cmd.setIndex(index);
		return cmd;
  }

	protected Command getAddCommand(ChangeBoundsRequest request)
	{
		CompoundCommand command = new CompoundCommand();
		command.setDebugLabel("Add in DrawComponentTreeContainerEditPolicy");//$NON-NLS-1$
		List editparts = request.getEditParts();
		int index = findIndexOfTreeItemAt(request.getLocation());
		
		for(int i = 0; i < editparts.size(); i++) 
		{
		  EditPart child = (EditPart)editparts.get(i);
			if(isAncestor(child,getHost()))
			  command.add(UnexecutableCommand.INSTANCE);
			else {
			  DrawComponent childModel = (DrawComponent)child.getModel();
				command.add(createCreateCommand(
							childModel,
							new Rectangle(
								new org.eclipse.draw2d.geometry.Point(childModel.getX(), childModel.getY()),
								new Dimension(childModel.getWidth(), childModel.getHeight())
							),
							index, "Reparent DrawComponent"));//$NON-NLS-1$
			}
		}
		return command;
	}

	protected Command getCreateCommand(CreateRequest request)
	{
		DrawComponent child = (DrawComponent)request.getNewObject();
		int index = findIndexOfTreeItemAt(request.getLocation());
//		return createCreateCommand(child, null, index, EditorPlugin.getResourceString("command_create_drawcomponent"));//$NON-NLS-1$
		Rectangle bounds = J2DUtil.toDraw2D(child.getBounds());
		return createCreateCommand(child, bounds, index, EditorPlugin.getResourceString("command_create_drawcomponent"));//$NON-NLS-1$
	}

	protected Command getMoveChildrenCommand(ChangeBoundsRequest request)
	{
		CompoundCommand command = new CompoundCommand();
		List editparts = request.getEditParts();
		List children = getHost().getChildren();
		int newIndex = findIndexOfTreeItemAt(request.getLocation());
		
		for(int i = 0; i < editparts.size(); i++)
		{
			EditPart child = (EditPart)editparts.get(i);
			int tempIndex = newIndex;
			int oldIndex = children.indexOf(child);
			if(oldIndex == tempIndex || oldIndex + 1 == tempIndex) {
			  command.add(UnexecutableCommand.INSTANCE);
			  return command;
			} else if(oldIndex <= tempIndex) {
			  tempIndex--;
			}
			command.add(new DrawComponentReorderCommand(
					(DrawComponent)child.getModel(), 
					(DrawComponentContainer)getHost().getModel(), 
					tempIndex));
		}
		return command;
	}

	protected boolean isAncestor(EditPart source, EditPart target)
	{
		if(source == target)
		  return true;
		if(target.getParent() != null)
		  return isAncestor(source, target.getParent());
		return false;
	}

}
