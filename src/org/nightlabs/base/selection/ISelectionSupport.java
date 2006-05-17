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
import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public interface ISelectionSupport
extends ISelectionProvider
{
//	/**
//	 * fires a selectionEvent to all registered SelectionListeners
//	 *
//	 */
//	void fireSelectionChanged();
	
	/**
	 * clears the selection
	 */
	void clearSelection();

	/**
	 * @return the Selection as StructuredSelection
	 */
	StructuredSelection getSelectedObjects();		
	
	/**
	 * @param selectedObjects A List of Objects which are selected
	 */
	void setSelection(List selectedObjects);
	
	/**
	 * @param o the Object to add to the selection
	 */
	void addSelectedObject(Object o);	
	
	/**
	 * @param objects a Collection of Objects to add to the selection
	 */
	void addSelectedObjects(Collection objects);
	
	/**
	 * @param o the Object to remove from the selection
	 */
	void removeSelectedObject(Object o);
	
	/**
	 * @param objects a Collection of Objects to remove from the selection
	 */
	void removeSelectedObjects(Collection objects);
	
	/**
	 * @param o element whose presence in this collection is to be tested
	 * @return true if the Object is contained, false if not
	 */
	boolean contains(Object o);	
}
