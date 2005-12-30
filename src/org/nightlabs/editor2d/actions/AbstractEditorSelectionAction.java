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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;

import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.MultiLayerDrawComponent;

public abstract class AbstractEditorSelectionAction 
extends SelectionAction
{

	public AbstractEditorSelectionAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	public AbstractEditorSelectionAction(AbstractEditor editor) {
		super(editor);
	}

	protected abstract boolean calculateEnabled(); 

	protected boolean isActiveEditor()
	{
		if (getWorkbenchPart().getSite().getWorkbenchWindow().getActivePage().getActiveEditor().equals(getEditor())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return the AbstractEditor
	 * @see org.nightlabs.editor2d.AbstractEditor
	 */
	public AbstractEditor getEditor() {
		return (AbstractEditor) getWorkbenchPart();
	}
	
	/**
	 * 
	 * @return the MultiLayerDrawComponent of the AbstractEditor
	 * @see org.nightlabs.editor2d.MultiLayerDrawComponent
	 */
	public MultiLayerDrawComponent getMultiLayerDrawComponent() 
	{
		return getEditor().getMultiLayerDrawComponent();
	}	
	
	/**
	 * 
	 * @param clazz the Class to search for 
	 * @param amount the minimum amount of occurences of the given class 
	 * in the selected objects #
	 * @param model determines if the Class-Check should be performed on the
	 * selected EditParts or the model (DrawComponent) of the EditParts
	 * @return true if the selected objects contain minimum so many EditPart
	 * as the given amount of the given class
	 */
	protected boolean selectionContains(Class clazz, int amount, boolean model) 
	{
		if (!getSelectedObjects().isEmpty()) 
		{
			int counter = 0;
			for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) 
			{
				EditPart editPart = (EditPart) it.next();
				Class c = null;
				if (!model) 
					c = editPart.getClass();
				else
					c = editPart.getModel().getClass();
				
				if (clazz.isAssignableFrom(c)) {
					counter++;
					if (amount == counter)
						return true;
				}					
			}
		}
		return false;
	}
	
	/**
	 * A Convenice Method which calls selectionContains with the amount 1 
	 * @see selectionContains(Class clazz, int amount, boolean model)
	 */
	protected boolean selectionContains(Class clazz, boolean model) {
		return selectionContains(clazz, 1, model);
	}
	
	protected static List EMPTY_LIST = new LinkedList(); 
	
	/**
	 * 
	 * @param clazz the Class to search for
	 * @param model determines if the Class-Check should be performed on the
	 * selected EditParts or the model (DrawComponent) of the EditParts
	 * @return a List of all objects from the selection which are assignable 
	 * from the given class
	 */
	protected List getSelection(Class clazz, boolean model) 
	{
		if (!getSelectedObjects().isEmpty()) 
		{
			List selection = new ArrayList();
			for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) 
			{
				EditPart editPart = (EditPart) it.next();
				Class c = null;
				if (!model)
					c = editPart.getClass();
				else
					c = editPart.getModel().getClass();
				
				if (clazz.isAssignableFrom(c)) 
				{
					if (!model)
						selection.add(clazz.cast(editPart));
					else
						selection.add(clazz.cast(editPart.getModel()));
				}
			}
			return selection;
		}
		return EMPTY_LIST;
	}
	
	protected IStructuredSelection getStructuredSelection() {
		return (IStructuredSelection) getSelection();
	}
	
	protected EditPart getPrimarySelected() {
		return (EditPart) getStructuredSelection().getFirstElement();
	}
	
	/**
	 * 
	 * @return the GraphicalViewer of the AbstractEditor
	 * @see org.eclipse.gef.GraphicalViewer
	 */
	public GraphicalViewer getGraphicalViewer() {
		return getEditor().getOutlineGraphicalViewer();		
	}
	
	/**
	 * 
	 * @param model the DrawComponent to find its EditPart
	 * @return the corresponding EditPart
	 * 
	 */
	public EditPart getEditPart(DrawComponent model) {
		return (EditPart) getGraphicalViewer().getEditPartRegistry().get(model);
	}
	
	/**
	 * 
	 * @param drawComponents a List of DrawComponents to find a EditParts for
	 * @return a List of the corresponding EditParts
	 */
	public List getEditParts(List drawComponents) 
	{
		List editParts = new ArrayList();
		for (Iterator it = drawComponents.iterator(); it.hasNext(); ) 
		{
			DrawComponent dc = (DrawComponent) it.next();
			EditPart ep = getEditPart(dc);
			editParts.add(ep);
		}
		return editParts;
	}
	
	/**
	 * selects the EditParts in the GraphicalViewer for the the given List
	 * of drawComponents  
	 * @param drawComponents
	 */
	public void selectEditPart(List drawComponents) 
	{
		List editParts = getEditParts(drawComponents);
		getGraphicalViewer().setSelection(new StructuredSelection(editParts));
	}
	
	/**
	 * 
	 * @return the Shell of the Site for the AbstractEditor
	 */
	public Shell getShell() {
		return getEditor().getSite().getShell();
	}	
}
