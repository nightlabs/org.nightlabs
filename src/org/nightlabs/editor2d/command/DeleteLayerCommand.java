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

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.MultiLayerDrawComponent;


public class DeleteLayerCommand 
extends Command 
{
	/** The DrawComponent to delete */
	private Layer child;		
	/** MultiLayerDrawComponent to removed from. */
	private final MultiLayerDrawComponent parent;
	/** True, if child was removed from its parent. */	
	private boolean wasRemoved;
	/** the LayerIndex of the MultiLayerDrawComponent */
	private int layerIndex;
	/** the Deletion String */
	public static final String DELETE_LAYER = EditorPlugin.getResourceString("command_delete_layer");
	/**
	 * Create a command that will remove the shape from its parent.
	 * @param parent the ShapesDiagram containing the child
	 * @param child    the Shape to remove
	 * @throws IllegalArgumentException if any parameter is null
	 */ 
	public DeleteLayerCommand(MultiLayerDrawComponent parent, Layer child)	
	{
		if (parent == null || child == null) {
			throw new IllegalArgumentException();
		}
		setLabel(DELETE_LAYER);
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
//	  layerIndex = parent.getDrawComponents().indexOf(child);
//	  wasRemoved = parent.getDrawComponents().remove(child);
		layerIndex = parent.getDrawComponents().indexOf(child);
		wasRemoved = parent.getDrawComponents().remove(child);		
		setCurrentLayer();
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() 
	{
	  parent.getDrawComponents().remove(child);	 
	  setCurrentLayer();	  
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() 
	{
	  parent.getDrawComponents().add(layerIndex, child);
	  parent.setCurrentLayer(child);
	  
//		Layer l = (Layer) parent.getDrawComponents().get(layerIndex);
//		l.getDrawComponents().add(layerIndex, child);
//		setCurrentLayer();		
	}
	
	protected void setCurrentLayer() 
	{
    if (layerIndex != 0) {
      parent.setCurrentLayer((Layer) parent.getDrawComponents().get(layerIndex-1));
    } else if ( layerIndex==0 && parent.getDrawComponents().size() > 2) {
      parent.setCurrentLayer((Layer) parent.getDrawComponents().get(layerIndex+1));
    }			  	  
	}
	
	public MultiLayerDrawComponent getParent() {
	  return parent;
	}
	
}
