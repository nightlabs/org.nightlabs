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
package org.nightlabs.base.composite;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * This class provides a composite able to display a list of objects of the same type as graphical representation.
 * The labels of the objects are provided by a {@link ILabelProvider}.
 * 
 * @author Tobias Langner <tobias[DOT]langner[AT]nightlabs[DOT]de>
 * 
 * @param <T> The type of the elements that should be displayed inside this list.
 */
public class ListComposite<T> extends XComposite 
{
	/**
	 * List of the objects that should be displayed in the list.
	 */
	private List<T> elements = null;
	
	/**
	 * Label provider for the elements in the list.
	 */
	private ILabelProvider labelProvider;
	
	private org.eclipse.swt.widgets.List list;

	/**
	 * Creates a new instance of {@link ListComposite} and uses a default LabelProvider and default
	 * layout definitions.
	 * 
	 * @param elements The elements to be displayed in the List.
	 * @param parent The parent composite.
	 * @param style the SWT style flag.	 
	 */
	public ListComposite(List<T> elements, Composite parent, int style)
	{
		this(elements, new LabelProvider(), parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.NONE);
	}
	
	/**
	 * Creates a new instance of {@link ListComposite} using default layout definitions.
	 * 
	 * @param elements The elements to be displayed in the List.
	 * @param labelProvider The {@link ILabelProvider} that provides the label of the list elements.
	 * @param parent The parent composite.
	 * @param style the SWT style flag.	 
	 */
	public ListComposite(List<T> elements, ILabelProvider labelProvider, Composite parent, int style)
	{
		this(elements, labelProvider, parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.NONE);
	}
	
	/**
	 * Creates a new instance of {@link ListComposite} and uses a default LabelProvider.
	 * 
	 * @param elements The elements to be displayed in the List.
	 * @param parent The parent composite.
	 * @param style the SWT style flag.	 
	 * @param layoutMode The {@link LayoutMode} to set.
	 * @param layoutDataMode The {@link LayoutDataMode} to set.
	 */
	public ListComposite(List<T> elements, Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		this(elements, new LabelProvider(), parent, style, layoutMode, layoutDataMode);
	}
	
	/**
	 * Creates a new instance of {@link ListComposite}.
	 * 
	 * @param elements The elements to be displayed in the List.
	 * @param labelProvider The {@link ILabelProvider} that provides the label of the list elements.
	 * @param parent The parent composite.
	 * @param style the SWT style flag.	 
	 * @param layoutMode The {@link LayoutMode} to set.
	 * @param layoutDataMode The {@link LayoutDataMode} to set.
	 */
	public ListComposite(List<T> elements, ILabelProvider labelProvider, Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.elements = elements;
		this.labelProvider = labelProvider;
		
		list = new org.eclipse.swt.widgets.List(this, SWT.BORDER);
		list.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		populateList();
	}
	
	/**
	 * Returns the encapsulated graphical representation, {@link org.eclipse.swt.widgets.List}.
	 */
	public org.eclipse.swt.widgets.List getList()
	{
		return list;
	}
	
	/**
	 * Populates the graphical list with the elements provided and labels them using the label provider.
	 */
	protected void populateList()
	{		
		for (T elem : elements)
			list.add(labelProvider.getText(elem));
	}
	
	/**
	 * Adds the specified element to the end of the list.
	 * @param element The element to be added.
	 */
	public void addElement(T element)
	{
		elements.add(element);
		list.add(labelProvider.getText(element));		
	}
	
	/**
	 * Removes the specified element from the list.
	 * @param element The element to be removed.
	 */
	public void removeElement(T element)
	{
		int index = elements.indexOf(element);		
		list.remove(index);
		elements.remove(index);
	}
	
	/**
	 * Removes all elements from the list.
	 */
	public void removeAll()
	{
		list.removeAll();
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
	 * Returns the object that is currently selected in the list.
	 * @return the currently selected object.
	 */
	public T getSelectedElement()
	{
		int selIndex = list.getSelectionIndex();
		if (selIndex != -1)
			return elements.get(selIndex);
		else
			return null;
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
			list.setSelection(index);
			list.showSelection();
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Refreshes the list.
	 *
	 */
	public void refresh()
	{
		list.removeAll();
		populateList();
	}

	/**
	 * Removes the currently selected element in the list and returns the removed element.
	 */
	public T removeSelected()
	{
		int index = list.getSelectionIndex();
		T toReturn = getSelectedElement();
		elements.remove(index);
		list.remove(index);
		list.select(Math.min(index, list.getItemCount()-1));
		list.showSelection();
		
		return toReturn;
	}

	/**
	 * Refreshes the label of the given element. 
	 * @param valueName The element to refresh.
	 */
	public void refreshElement(T element)
	{
		int index = elements.indexOf(element);
		if (index != -1)
		{
			list.setItem(index, labelProvider.getText(element));
		}
	}
}