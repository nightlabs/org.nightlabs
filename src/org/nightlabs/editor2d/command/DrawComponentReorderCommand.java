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


public class DrawComponentReorderCommand 
extends Command 
{
  private int oldIndex, newIndex;
  private DrawComponent child;
  private DrawComponentContainer parent;

  public DrawComponentReorderCommand(DrawComponent child, DrawComponentContainer parent, int newIndex ) 
  {
  	super(EditorPlugin.getResourceString("command_reorder_drawcomponent"));
  	this.child = child;
  	this.parent = parent;
  	this.newIndex = newIndex;
  }

  public void execute() 
  {
  	oldIndex = parent.getDrawComponents().indexOf(child);
  	parent.getDrawComponents().remove(child);
  	parent.getDrawComponents().add(newIndex, child);
  }

  public void undo() 
  {
  	parent.getDrawComponents().remove(child);
  	parent.getDrawComponents().add(oldIndex, child);
  }
}
