/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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
package org.nightlabs.editor2d.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.command.CreateDrawComponentCommand;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class CopyAction 
//extends SelectionAction 
extends AbstractEditorSelectionAction
{
	public static final String ID = CopyAction.class.getName();
	
	/**
	 * @param part
	 * @param style
	 */
	public CopyAction(AbstractEditor part, int style) {
		super(part, style);
	}

	/**
	 * @param part
	 */
	public CopyAction(AbstractEditor part) {
		super(part);
	}

  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.copy.text"));
  	setToolTipText(EditorPlugin.getResourceString("action.copy.tooltip"));
  	setId(ID);
//  	setImageDescriptor(ImageDescriptor.createFromFile(EditorPlugin.class,"icons/editShape16.gif"));
  } 	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() 
	{
		return !getSelectedObjects().isEmpty();
	}

	public void run() 
	{
		List dcs = getSelection(DrawComponent.class, true);
		Command cmd = new CompoundCommand();
		for (Iterator it = dcs.iterator(); it.hasNext(); ) {
			DrawComponent dc = (DrawComponent) it.next();
			CreateDrawComponentCommand createCmd = new CreateDrawComponentCommand();
			DrawComponent clone = dc.clone();
			clone.setName(clone.getName() + getCopyString());
			createCmd.setChild(clone);
			createCmd.setParent(dc.getParent());
			
			cmd.chain(createCmd);
		}
		execute(cmd);
	}
	
	protected String getCopyString() 
	{
		return " ("+EditorPlugin.getResourceString("action.copy.text")+")";
	}
}
