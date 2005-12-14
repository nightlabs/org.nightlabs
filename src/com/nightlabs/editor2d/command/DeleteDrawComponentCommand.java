/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.command;

import org.eclipse.gef.commands.Command;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.DrawComponentContainer;
import com.nightlabs.editor2d.EditorPlugin;

public class DeleteDrawComponentCommand 
extends Command
{
	/** The DrawComponent to delete */
	protected DrawComponent child;
	
	/** MultiLayerDrawComponent to removed from. */
	protected final DrawComponentContainer parent;
  
	/** the DrawComponentsIndex of the DrawComponentContainer */
	protected int index;
	
	/** True, if child was removed from its parent. */	
	protected boolean wasRemoved;

	/** the Deletion String */
	public static final String DELETE_DRAWCOMPONENT = EditorPlugin.getResourceString("command_delete_drawcomponent");
  
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