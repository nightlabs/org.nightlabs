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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.command.DeleteDrawComponentCommand;
import org.nightlabs.editor2d.impl.LayerImpl;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class DeleteAction 
extends AbstractEditorSelectionAction 
{
	public static final String ID = ActionFactory.DELETE.getId();
	
	public static final Logger LOGGER = Logger.getLogger(DeleteAction.class);
	
	/**
	 * @param editor
	 * @param style
	 */
	public DeleteAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public DeleteAction(AbstractEditor editor) {
		super(editor);
	}
		
	@Override
	protected boolean calculateEnabled() 
	{
		if (getSelectedObjects().isEmpty())
			return false;
	
		Collection<DrawComponent> selection = getSelection(DrawComponent.class, true);
		
		if (onlyOnePage()) {
			if (selection.contains(getOnlyPage()))
				return false;
		}

		if (onlyOneLayer()) {
			if (selection.contains(getOnlyLayer()))
				return false;
		}

		if (selection.contains(getMultiLayerDrawComponent()))
			return false;
		
		return true;
	}

	protected boolean onlyOnePage() {
		return !(getMultiLayerDrawComponent().getDrawComponents().size() > 1);
	}
		
	protected DrawComponent getOnlyPage() {
		return getMultiLayerDrawComponent().getDrawComponents().get(0);
	}
	
	protected boolean onlyOneLayer() {
		return !(getMultiLayerDrawComponent().getDrawComponents(LayerImpl.class).size() > 1);
	}
	
	protected DrawComponent getOnlyLayer() {
		return getMultiLayerDrawComponent().getDrawComponents(LayerImpl.class).get(0);
	}
	
	@Override
	protected void init() 
	{
		setId(ID);
		setText(EditorPlugin.getResourceString("action.delete.text"));
		setToolTipText(EditorPlugin.getResourceString("action.delete.tooltip"));
		setActionDefinitionId("org.eclipse.ui.edit.delete");		
//		ISharedImages sharedImages = getWorkbenchPart().getSite().getPage().getWorkbenchWindow().getWorkbench().getSharedImages();		
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();		
  	setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
  	setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
	}

	@Override
	public void run() 
	{
		CompoundCommand compound = new CompoundCommand();
		compound.setLabel(EditorPlugin.getResourceString("command.delete.drawcomponent"));
		Collection<DrawComponent> selection = getSelection(DrawComponent.class, true);
		for (DrawComponent dc : selection) {
			DeleteDrawComponentCommand deleteCmd = new DeleteDrawComponentCommand(dc.getParent(), dc);
			compound.add(deleteCmd);
		}
		execute(compound);
	}
		
}
