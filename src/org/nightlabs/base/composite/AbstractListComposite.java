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
 ******************************************************************************/
package org.nightlabs.base.composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This class provides abstract logic for managing a list of objects and displaying them in
 * list-style gui controls as e.g. combo boxes or lists.
 * 
 * The labels of the objects are provided by a {@link ILabelProvider}.
 * 
 * @author Tobias Langner <tobias[DOT]langner[AT]nightlabs[DOT]de>
 * 
 * @param <T> The type of the elements that should be displayed inside this combo.
 */
public abstract class AbstractListComposite<T> extends XComposite
implements ISelectionProvider
{	
	/**
	 * List of the objects that should be managed.
	 */
	protected List<T> elements = null;
	
	/**
	 * Label provider for the managed elements.
	 */
	protected ILabelProvider labelProvider;
	
	/**
	 * Creates a new instance and uses a default LabelProvider and default layout definitions.
	 * @param parent The parent composite.
	 * @param style the SWT style flag.	 
	 */
	public AbstractListComposite(Composite parent, int style)
	{
		this(new LabelProvider(), parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA);		
	}
	
	/**
	 * Creates a new instance using default layout definitions.
	 * @param labelProvider The {@link ILabelProvider} that provides the label of the list elements.
	 * @param parent The parent composite.
	 * @param style the SWT style flag.	 
	 */
	public AbstractListComposite(ILabelProvider labelProvider, Composite parent, int style)
	{
		this(labelProvider, parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA);
	}
	
	public AbstractListComposite(ILabelProvider labelProvider, Composite parent, int style, boolean doCreateGuiComposite)
	{
		this(labelProvider, parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, doCreateGuiComposite, false);
	}
	
	/**
	 * Creates a new instance and uses a default LabelProvider.
	 * @param parent The parent composite.
	 * @param style the SWT style flag.	 
	 * @param layoutMode The {@link LayoutMode} to set.
	 * @param layoutDataMode The {@link LayoutDataMode} to set.
	 */
	public AbstractListComposite(Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode)
	{
		this(new LabelProvider(), parent, style, layoutMode, layoutDataMode);
	}
	
	/**
	 * Creates a new instance.
	 * @param labelProvider The {@link ILabelProvider} that provides the label of the managed elements..
	 * @param parent The parent composite.
	 * @param style the SWT style flag.	 
	 * @param layoutMode The {@link LayoutMode} to set.
	 * @param layoutDataMode The {@link LayoutDataMode} to set.
	 */
	public AbstractListComposite(ILabelProvider labelProvider, Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode)
	{
		this(labelProvider, parent, style, layoutMode, layoutDataMode, true, false);
	}
	
	public AbstractListComposite(ILabelProvider labelProvider, Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode, boolean doCreateGuiControl, boolean createLabel)
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.labelProvider = labelProvider;
		
		elements = new LinkedList<T>();
		
		if (doCreateGuiControl)
			createGuiControl(this, style | SWT.BORDER, createLabel);
	}

	protected abstract Control createGuiControl(Composite parent, int style, boolean createLabel);
	
	/**
	 * Populates the graphical list with the elements provided and labels them using the label provider.
	 */
	protected void populateList()
	{
		int index = 0;
		for (T elem : elements)
			addElementToGui(index++, elem);
	}

	/**
	 * The given element is added to the graphical representation of this list.
	 * @param index TODO
	 * @param element The element to be added.
	 */
	protected abstract void addElementToGui(int index, T element);
	
	/**
	 * The given element is removed from the graphical representation of this list.
	 * @param index The element to be removed.
	 */
	protected abstract void removeElementFromGui(int index);
	
	/**
	 * All elements are removed from the graphical representation of this list.
	 */
	protected abstract void removeAllElementsFromGui();
	
	/**
	 * Returns the index of the element currently selected in the graphical representation of this list.
	 */
	protected abstract int getSelectionIndex();

	protected abstract int[] getSelectionIndices();

	/**
	 * Selects the element with the given index in the graphical representation of this list.
	 */
	protected abstract void setSelection(int index);

	protected abstract void setSelection(int[] indices);

	protected abstract int getSelectionCount();

	/**
	 * Refreshes the specified element and updates its label.
	 * @param The element to be refreshed.
	 */
	protected abstract void refreshElement(T elem);	
	
	/**
	 * Adds the specified element to the end of the list.
	 * @param element The element to be added.
	 */
	public void addElement(T element)
	{
		elements.add(element);
		addElementToGui(elements.size() - 1, element);
	}

	public void addElement(int index, T element)
	{
		elements.add(index, element);
		addElementToGui(index, element);
	}
	
	/**
	 * Removes the specified element from the list.
	 * @param element The element to be removed.
	 */
	public int removeElement(T element)
	{
		int index = elements.indexOf(element);
		removeElementFromGui(index);
		elements.remove(index);
		return index;
	}

	/**
	 * Removes all elements from the list.
	 */
	public void removeAll()
	{
		removeAllElementsFromGui();
		elements.clear();
	}
	
	/**
	 * Adds all specified elements to the list.
	 * @param elements The elements to be added.
	 */
	public void addElements(List<T> elements)
	{
		for (T elem : elements)
			addElement(elem);
	}
	
	/**
	 * Resets the list to the specified elements.
	 * @param elements The elements to be set.
	 */
	public void setInput(List<T> elements)
	{
		removeAll();
		addElements(elements);
	}
	
	/**
	 * Returns the object that is currently selected in the list.
	 * @return the currently selected object.
	 */
	public T getSelectedElement()
	{		
		int selIndex = getSelectionIndex();
		if (selIndex != -1)
			return elements.get(selIndex);
		else
			return null;
	}

	public List<T> getSelectedElements()
	{
		ArrayList<T> selectedElements = new ArrayList<T>(getSelectionCount());
		int[] selIndices = getSelectionIndices();
		for (int i = 0; i < selIndices.length; ++i) {
			selectedElements.add(elements.get(selIndices[i]));
		}
		return selectedElements;
	}

	/**
	 * Returns the index of the given element.
	 * @param element The element whose index is to be returned.
	 * @return The index of the element.
	 */
	protected int getElementIndex(T element)
	{
		return elements.indexOf(element);
	}

	/**
	 * Returns the current list of elements.
	 * 
	 * @return The current list of elements.
	 */
	public List<T> getElements() {
		return elements;
	}
	
	/**
	 * Checks whether the given element is within the current element list
	 * 
	 * @param element The element to check
	 * @return Whether or not the given element is within the current element list
	 */
	public boolean contains(T element) {
		return elements.contains(element);
	}
	
	/**
	 * Selects the given element in the list.
	 * 
	 * @param element The element to be selected.
	 * @return <code>True</code> if the element to be selected was in the list and <code>false</code> if not.
	 */
	public boolean selectElement(T element)
	{
		int index = elements.indexOf(element);
		if (index != -1)
		{
			setSelection(index);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Refreshes the list.
	 *
	 */
	protected void reset()
	{
		removeAllElementsFromGui();
		populateList();
	}
	
	/**
	 * Removes the currently selected element in the list and returns the removed element.
	 */
	public T removeSelected()
	{
		int index = getSelectionIndex();
		T toReturn = getSelectedElement();
		elements.remove(index);
		removeElementFromGui(index);
		setSelection(Math.min(index, elements.size()-1));
		
		return toReturn;
	}
	
	public void refresh()
	{
		for (T elem : elements)
			refreshElement(elem);
	}

	private ListenerList selectionChangedListeners = new ListenerList();

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	/**
	 * @return an instance of {@link IStructuredSelection} containing all selected elements.
	 *
	 * @see ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		return new StructuredSelection(getSelectedElements());
	}

	/**
	 * @param selection This must be an instance of {@link IStructuredSelection} containing the elements
	 *		you want to select (all others will be deselected. If the concrete implementation of {@link AbstractListComposite}
	 *		does not support multi-selections, the first element of <code>selection</code> will be selected.
	 *
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	public void setSelection(ISelection selection)
	{
		IStructuredSelection sel = (IStructuredSelection) selection;
		LinkedList<Integer> selectionIndices = new LinkedList<Integer>();
		for (Iterator it = sel.iterator(); it.hasNext(); ) {
			Object element = it.next();
			int index = elements.indexOf(element);
			if (index < 0)
				throw new IllegalArgumentException("The object in the selection is not known in this list: " + element);

			selectionIndices.add(index);
		}

		int[] si = new int[selectionIndices.size()]; int i = 0;
		for (Integer selIdx : selectionIndices) {
			si[i++] = selIdx.intValue();
		}

		setSelection(si);
	}
}