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
