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


import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;

/**
 * This class provides a composite able to display a list of objects of the same type as graphical representation.
 * The labels of the objects are provided by a {@link ILabelProvider}.
 * 
 * @author Tobias Langner <tobias[DOT]langner[AT]nightlabs[DOT]de>
 * 
 * @param <T> The type of the elements that should be displayed inside this list.
 */
public class ListComposite<T> extends AbstractListComposite<T> 
{
	private List list;
	
	public ListComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);
	}

	public ListComposite(Composite parent, int style)
	{
		super(parent, style);
	}

	public ListComposite(ILabelProvider labelProvider, Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(labelProvider, parent, style, layoutMode, layoutDataMode);
	}

	public ListComposite(ILabelProvider labelProvider, Composite parent, int style)
	{
		super(labelProvider, parent, style);
	}

	@Override
	protected void addElementToGui(T element)
	{
		list.add(labelProvider.getText(element));
	}

	@Override
	protected Control createGuiControl(Composite parent, int style)
	{
		list = new List(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
		if (parent.getLayout() instanceof GridLayout)
			list.setLayoutData(new GridData(GridData.FILL_BOTH));
		return list;
	}

	@Override
	protected int getSelectedIndex()
	{
		return list.getSelectionIndex();
	}

	@Override
	protected void removeAllElementsFromGui()
	{
		list.removeAll();
	}

	@Override
	protected void removeElementFromGui(int index)
	{
		list.remove(index);
	}

	@Override
	protected void setSelection(int index)
	{
		list.select(index);
		list.showSelection();
	}
	
	public List getList()
	{
		return list;
	}
	
	/**
	 * @see AbstractListComposite#refreshElement(T)
	 */
	public void refreshElement(T elem)
	{
		int index = getElementIndex(elem);
		list.remove(index);
		list.add(labelProvider.getText(elem), index);
	}	
}