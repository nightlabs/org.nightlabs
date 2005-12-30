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

import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.MultiLayerDrawComponent;


public class CreateLayerCommand  
extends CreateDrawComponentCommand
{
  public static final Logger LOGGER = Logger.getLogger(CreateLayerCommand.class);
  
  private static final String LAYER_DEFAULT_NAME = EditorPlugin.getResourceString("layer_default_name");
	
	private int layerCount = 0; 
	protected int nextLayerCount() {
	  return layerCount++;
	}
	 
	public CreateLayerCommand(MultiLayerDrawComponent parent)
	{
		if (parent == null) {
			throw new IllegalArgumentException("Param parent (MultiLayerDrawComponent) must not be null!");
		}
	  
	  this.parent = parent;
	  setLabel(EditorPlugin.getResourceString("command_create_layer"));	  
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() 
	{
	  drawComponent = Editor2DFactory.eINSTANCE.createLayer();
	  
    getLayer().setParent(getMultiLayerDrawComponent());
		drawOrderIndex = getMultiLayerDrawComponent().getDrawComponents().indexOf(
        getMultiLayerDrawComponent().getCurrentLayer()) + 1;    
    getMultiLayerDrawComponent().addDrawComponent(getLayer(), drawOrderIndex);
		shapeAdded = true;
		if (layerCount == 0) {
		  layerCount = getMultiLayerDrawComponent().getDrawComponents().size();
		}
    getMultiLayerDrawComponent().setCurrentLayer(getLayer());
    // TODO should come from somewhere else
//		getLayer().setName(LAYER_DEFAULT_NAME + nextLayerCount());	
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() 
	{
    super.redo();
		getMultiLayerDrawComponent().setCurrentLayer(getLayer());			  		
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() 
	{
    super.undo();
		layerCount--;
	}		
		
	protected MultiLayerDrawComponent getMultiLayerDrawComponent() {
	  return (MultiLayerDrawComponent) parent;
	}
  
  protected Layer getLayer() {
    return (Layer) drawComponent;
  }
}
