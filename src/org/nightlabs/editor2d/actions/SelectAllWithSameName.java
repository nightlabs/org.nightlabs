/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 11.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
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
