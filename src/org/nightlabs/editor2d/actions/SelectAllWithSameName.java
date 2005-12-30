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

package org.nightlabs.editor2d.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;

import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;

public class SelectAllWithSameName 
extends AbstractEditorSelectionAction
{
	public static final String ID = SelectAllWithSameName.class.getName(); 
	
	public SelectAllWithSameName(AbstractEditor editor, int style) 
	{
		super(editor, style);
	}

	public SelectAllWithSameName(AbstractEditor editor) 
	{
		super(editor);
	}

	protected EditPart editPart = null;
	protected List drawComponentsWithSameName = null;
	
	protected boolean calculateEnabled() 
	{
		if (getSelectedObjects().size() == 1) 
		{
			editPart = (EditPart) getSelectedObjects().get(0);
			DrawComponent dc = (DrawComponent) editPart.getModel();
			String name = dc.getName(); 
			Class c = dc.getClass();
			List drawComponents = getMultiLayerDrawComponent().getDrawComponents(c);
			drawComponentsWithSameName = getDrawComponentsWithSameName(drawComponents, name);
			if (!drawComponentsWithSameName.isEmpty())
				return true;				
		}
		return false;
	}

	public List getDrawComponentsWithSameName(Collection drawComponents, String name) 
	{
		List drawComponentsWithSameName = new ArrayList();
		for (Iterator it = drawComponents.iterator(); it.hasNext(); ) 
		{
			DrawComponent dc = (DrawComponent) it.next();
			String dcName = dc.getName();
			if (dcName.equals(name))
				drawComponentsWithSameName.add(dc);
		}
		return drawComponentsWithSameName;		
	}

	public void run() 
	{
		selectEditPart(drawComponentsWithSameName);
	}

	protected void init() 
	{
		setId(ID);
		setText(EditorPlugin.getResourceString("action.selectAllWithSameName.text"));
		setToolTipText(EditorPlugin.getResourceString("action.selectAllWithSameName.tooltip"));
	}
		
}
