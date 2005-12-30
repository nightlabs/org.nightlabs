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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionSupport
extends SelectionProviderImpl
implements ISelectionSupport
{

	public SelectionSupport() {
		super();
	}

	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#clearSelection()
	 */
	public void clearSelection() {
		setSelection(StructuredSelection.EMPTY);
	}

	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#setSelection(java.util.List)
	 */
	public void setSelection(List selectedObjects) 
	{
		StructuredSelection selection = new StructuredSelection(selectedObjects);
		setSelection(selection);
	}	
	
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#addSelectedObject(java.lang.Object)
	 */
	public void addSelectedObject(Object o) 
	{
		getSelectedObjectsList().add(o);
		selection = new StructuredSelection(getSelectedObjectsList());		
		fireSelectionChanged();
	}
	
	
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#addSelectedObjects(java.util.Collection)
	 */
	public void addSelectedObjects(Collection objects) 
	{
		if (!objects.isEmpty()) 
		{
			List l = getSelectedObjectsList(); 
			for (Iterator it = objects.iterator(); it.hasNext(); ) {
				l.add(it.next());
			}
			selection = new StructuredSelection(l);
			fireSelectionChanged();			
		}
	}	
	
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#removeSelectedObject(java.lang.Object)
	 */
	public void removeSelectedObject(Object o) 
	{
		List l = getSelectedObjectsList();
		l.remove(o);
		selection = new StructuredSelection(l);
		fireSelectionChanged();
	}	
	
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#removeSelectedObjects(java.util.Collection)
	 */
	public void removeSelectedObjects(Collection objects) 
	{
		if (!objects.isEmpty()) 
		{
			List l = getSelectedObjectsList();
			for (Iterator it = objects.iterator(); it.hasNext(); ) {
				l.remove(it.next());
			}
			selection = new StructuredSelection(l);
			fireSelectionChanged();
		}
	}
	
	/**
	 * @see ISelectionSupport.contains(o);
	 */
	public boolean contains(Object o) 
	{
		return getSelectedObjectsList().contains(o);
	}
}
