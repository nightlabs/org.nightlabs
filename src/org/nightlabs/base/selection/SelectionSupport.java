/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 19.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
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
		List l = getSelectedObjects().toList();
		l.add(o);
		selectedObjects = new StructuredSelection(l);		
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
			List l = getSelectedObjects().toList(); 
			for (Iterator it = objects.iterator(); it.hasNext(); ) {
				l.add(it.next());
			}
			selectedObjects = new StructuredSelection(l);
			fireSelectionChanged();			
		}
	}	
	
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#removeSelectedObject(java.lang.Object)
	 */
	public void removeSelectedObject(Object o) 
	{
		List l = getSelectedObjects().toList();
		l.remove(o);
		selectedObjects = new StructuredSelection(l);
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
			List l = getSelectedObjects().toList();
			for (Iterator it = objects.iterator(); it.hasNext(); ) {
				l.remove(it.next());
			}
			selectedObjects = new StructuredSelection(l);
			fireSelectionChanged();
		}
	}
}
