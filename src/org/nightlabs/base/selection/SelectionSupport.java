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
