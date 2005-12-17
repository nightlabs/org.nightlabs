/**
 * <p> Project: org.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 19.10.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.base.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.nightlabs.base.util.SelectionUtil;

public abstract class AbstractTypeSafeSelectionSupport
extends SelectionSupport
implements ISelectionSupport
{
	public AbstractTypeSafeSelectionSupport() {
		super();
	}
	
	/**
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection arg0) {
		selectedObjects = checkSelection(arg0);
		fireSelectionChanged();
	}
		
	/**
	 * 
	 * @param selection the ISelection to check
	 * @return a StructuredSelection which contains only entries which are
	 * assignable from getSelectionClass()
	 */
	protected StructuredSelection checkSelection(ISelection selection) 
	{
		return SelectionUtil.checkSelection(selection, getSelectionClass());
	}
	
	/**
	 * 
	 * @return the Base Class which determines which objects are allowed
	 * in the selection 
	 */
	public abstract Class getSelectionClass();
	
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#addSelectedObject(java.lang.Object)
	 */
	public void addSelectedObject(Object o) 
	{
		if (getSelectionClass().isAssignableFrom(o.getClass())) {
			super.addSelectedObject(o);
		}
	}
		
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#addSelectedObjects(java.util.Collection)
	 */
	public void addSelectedObjects(Collection objects) 
	{
		if (!objects.isEmpty()) 
		{
			List l = new ArrayList(objects.size());
			for (Iterator it = objects.iterator(); it.hasNext(); ) {
				Object o = it.next();
				if (getSelectionClass().isAssignableFrom(o.getClass())) {
					l.add(o);
				}
			}
			List oldSelectedObjects = getSelectedObjects().toList();
			oldSelectedObjects.addAll(l);
			selectedObjects = new StructuredSelection(oldSelectedObjects);
			fireSelectionChanged();
		}
	}
		
	/**
	 * 
	 * @see org.nightlabs.base.selection.ISelectionSupport#setSelection(java.util.List)
	 */
	public void setSelection(List selectedObjects) 
	{
		List list = SelectionUtil.checkList(selectedObjects, getSelectionClass());
		this.selectedObjects = new StructuredSelection(list);
		fireSelectionChanged();
	}
	
}
