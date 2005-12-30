/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionProviderImpl 
implements ISelectionProvider
{

	public SelectionProviderImpl() {
		super();
	}

	protected Collection selectionListeners;
	
	/**
	 * 
	 * @return a Collection which contains all added selectionChangedListeners
	 */
	protected Collection getSelectionListeners() 
	{
		if (selectionListeners == null)
			selectionListeners = new ArrayList();
		
		return selectionListeners;
	}
	
	/**
	 * fires a SelectionChangedEvent to all added SelectionChangedListeners
	 *
	 */
	protected void fireSelectionChanged() 
	{
		for (Iterator it = getSelectionListeners().iterator(); it.hasNext(); ) {
			ISelectionChangedListener l = (ISelectionChangedListener) it.next();
			l.selectionChanged(new SelectionChangedEvent(this, getSelection()));
		}
	}

	/**
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener arg0) {
		getSelectionListeners().add(arg0);
	}

	/**
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return getSelectedObjects();
	}

	/**
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener arg0) {
		getSelectionListeners().remove(arg0);
	}

	/**
	 * only accepts StructuredSelections
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection arg0) 
	{
		if (arg0 instanceof StructuredSelection) {
			selection = (StructuredSelection) arg0;
			selectedObjects = new ArrayList(selection.toList());
			fireSelectionChanged();			
		}		
	}

	protected StructuredSelection selection;
	protected List selectedObjects;
	
	protected List getSelectedObjectsList() 
	{
		if (selectedObjects == null)
			selectedObjects = new ArrayList();
		
		return selectedObjects;
	}
	
	/**
	 * 
	 * @return the selection as StructuredSelection
	 */
	public StructuredSelection getSelectedObjects() 
	{
		if (selectedObjects == null || selectedObjects.isEmpty())
			return StructuredSelection.EMPTY;
		
		return new StructuredSelection(selectedObjects);
	}
}
