/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 16.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
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
