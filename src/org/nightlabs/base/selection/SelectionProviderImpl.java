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
			selectedObjects = (StructuredSelection) arg0;
			fireSelectionChanged();			
		}		
	}

	protected StructuredSelection selectedObjects;
	
	/**
	 * 
	 * @return the selection as StructuredSelection
	 */
	public StructuredSelection getSelectedObjects() 
	{
		if (selectedObjects == null)
			selectedObjects = StructuredSelection.EMPTY;
		
		return selectedObjects;
	}
}
