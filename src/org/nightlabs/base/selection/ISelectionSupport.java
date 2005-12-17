/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 19.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
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
	 *
	 */
	void clearSelection();

	/**
	 * 
	 * @return the Selection as StructuredSelection
	 */
	StructuredSelection getSelectedObjects();		
	
	/**
	 * 
	 * @param selectedObjects A List of Objects which are selected
	 */
	void setSelection(List selectedObjects);
	
	/**
	 * 
	 * @param o the Object to add to the selectedObjects
	 */
	void addSelectedObject(Object o);	
	
	/**
	 * 
	 * @param objects a Collection of Objects to add to the selectedObjects
	 */
	void addSelectedObjects(Collection objects);
	
	/**
	 * 
	 * @param o the Object to remove from the selectedObjects
	 */
	void removeSelectedObject(Object o);
	
	/**
	 * 
	 * @param objects a Collection of Objects to remove from the selectedObjects
	 */
	void removeSelectedObjects(Collection objects);
}
