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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.command.CreateDrawComponentCommand;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PasteAction 
extends AbstractEditorAction
{
	public static final String ID = PasteAction.class.getName();
	
	/**
	 * @param part
	 */
	public PasteAction(AbstractEditor part) {
		super(part);
	}

	/**
	 * @param part
	 * @param style
	 */
	public PasteAction(AbstractEditor part, int style) {
		super(part, style);
	}

  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.paste.text"));
  	setToolTipText(EditorPlugin.getResourceString("action.paste.tooltip"));
  	setId(ID);
  } 
	
	protected boolean calculateEnabled() {
		return enabled;
	}

	protected boolean enabled = false;
	protected List clipBoardContent = null;	
	
	public void run() 
	{
		Command cmd = new CompoundCommand();
		if (clipBoardContent != null) 
		{
			for (Iterator it = clipBoardContent.iterator(); it.hasNext(); ) 
			{
				DrawComponent dc = (DrawComponent) it.next();
				DrawComponent clone = (DrawComponent) dc.clone();
				clone.setName(dc.getName() + getCopyString());
				CreateDrawComponentCommand createCmd = new CreateDrawComponentCommand();
				createCmd.setChild(clone);
				createCmd.setParent(getCurrentLayer());				
				cmd.chain(createCmd);
			}
			execute(cmd);					
		}
	}
		
	public IPropertyChangeListener copyListener = new IPropertyChangeListener()
	{	
		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) 
		{
			if (event.getProperty().equals(CopyAction.PROP_COPY_TO_CLIPBOARD)
					&& event.getSource() instanceof CopyAction) 
			{
//				Object content = Clipboard.getDefault().getContents();
				Object content = event.getNewValue();
				if (!content.equals(CopyAction.EMPTY_CLIPBOARD_CONTENT)) 
				{
					enabled = true;
					if (content instanceof List) 
					{
						clipBoardContent = (List) content;
					}
				}
			}			
		}	
	};
				
	protected String getCopyString() 
	{
		return " ("+EditorPlugin.getResourceString("action.copy.text")+")";
	}	
}
