/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.editpolicy.tree;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.MultiLayerDrawComponent;
import org.nightlabs.editor2d.command.CreateDrawComponentCommand;


public class MultiLayerDrawComponentTreeEditPolicy 
extends DrawComponentTreeContainerEditPolicy 
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
		
		MultiLayerDrawComponent mldc = (MultiLayerDrawComponent)getHost().getModel();
		Layer currentLayer = mldc.getCurrentLayer();
		cmd.setParent(currentLayer);

		cmd.setChild(child);
		cmd.setLabel(label);
		if(index >= 0)
		  cmd.setIndex(index);
		return cmd;
  }
}
