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

package org.nightlabs.editor2d.command;

import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorPlugin;

public class DeleteDrawComponentCommand 
extends Command
{
	/** The DrawComponent to delete */
	protected DrawComponent child;
	
	/** MultiLayerDrawComponent to removed from. */
	protected DrawComponentContainer parent;
  
	/** the DrawComponentsIndex of the DrawComponentContainer */
	protected int index;
	
	/** True, if child was removed from its parent. */	
	protected boolean wasRemoved;

	/** the Deletion String */
	public static final String DELETE_DRAWCOMPONENT = EditorPlugin.getResourceString("command.delete.drawcomponent");
  
	/**
	 * Create a command that will remove the shape from its parent.
	 * @param parent the ShapesDiagram containing the child
	 * @param child    the Shape to remove
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public DeleteDrawComponentCommand(DrawComponentContainer parent, DrawComponent child) 	
	{
		if (parent == null || child == null) {
			throw new IllegalArgumentException("Neither param parent not param child may be null!");
		}
		setLabel(DELETE_DRAWCOMPONENT);
		this.parent = parent;
		this.child = child;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return wasRemoved;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() 
	{
	  index = parent.getDrawComponents().indexOf(child);
	  if (index == -1)
	  	throw new IllegalStateException("DrawComponent "+child.getId()+" is not contained in DrawComponentContainer "+parent.getId());
    parent.removeDrawComponent(child);	  
    wasRemoved = true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() 
	{	  
    parent.removeDrawComponent(index);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() 
	{	  
    parent.addDrawComponent(child, index);    
	}
	
}
