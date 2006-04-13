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

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.util.J2DUtil;

public class CreateDrawComponentCommand 
extends Command
{
  public static final Logger LOGGER = Logger.getLogger(CreateDrawComponentCommand.class);
  
	/** The DrawComponent to add */
	protected DrawComponent drawComponent;
	/** DrawComponentContainer to add to. */
	protected DrawComponentContainer parent;
	/** True, if newDrawComponent was added to parent. */
	protected boolean shapeAdded;
	/** the DrawOrderIndex of the DrawComponentContainer */
	protected int drawOrderIndex;
	
	protected Rectangle rect;
	
	/**
	 * Create a command that will add a new DrawComponent to a MultiLayerDrawComponent.
	 * @param parent the MultiLayerDrawComponent that will hold the new element
	 * @param req     a request to create a new DrawComponent
	 * @throws IllegalArgumentException if any parameter is null, or the request
	 * 						  does not provide a new DrawComponent instance
	 */	
	public CreateDrawComponentCommand() 
	{
	  super(EditorPlugin.getResourceString("command.create.drawcomponent"));	  
	}
		
	/*
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return shapeAdded;
	}
	
	/*
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() 
	{
    drawComponent.setBounds(J2DUtil.toAWTRectangle(rect));
	  
    parent.addDrawComponent(drawComponent);
    shapeAdded = true;
		drawOrderIndex = parent.getDrawComponents().indexOf(drawComponent);
    
    if (drawComponent instanceof DrawComponentContainer) {
      ((DrawComponentContainer)drawComponent).setParent(parent);
    }
	}	
		
	/*
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() { 
    parent.addDrawComponent(drawComponent, drawOrderIndex);    
	}
		
	/*
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() { 
    parent.removeDrawComponent(drawComponent);
	}	
	
	public void setParent(DrawComponentContainer newParent) {
		parent = newParent;
	}	
	
	public void setLocation(Rectangle r) {
		rect = r;
	}	
	protected Rectangle getLocation() {
		return rect;
	}
	
	public void setChild(DrawComponent dc) {
		drawComponent = dc;
	}
	
	public DrawComponent getChild() {
	  return drawComponent;
	}
	
	public void setIndex(int index) {
	  this.drawOrderIndex = index;
	}  
}
