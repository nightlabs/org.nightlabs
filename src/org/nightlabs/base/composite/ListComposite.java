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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
	private Label label;

//	public ListComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
//	{
//		super(parent, style, layoutMode, layoutDataMode);
//	}

	public ListComposite(Composite parent, int style)
	{
		super(parent, style);
	}

//	public ListComposite(ILabelProvider labelProvider, Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
//	{
//		super(labelProvider, parent, style, layoutMode, layoutDataMode);
//	}

	public ListComposite(Composite parent, int style, ILabelProvider labelProvider)
	{
		super(parent, style, labelProvider, true, null);
	}

	public ListComposite(Composite parent, ILabelProvider labelProvider, boolean doCreateGuiComposite)
	{
		super(parent, labelProvider, doCreateGuiComposite, null);
	}

	public ListComposite(Composite parent, int style, ILabelProvider labelProvider, boolean doCreateGuiControl, String caption)
	{
		super(parent, style, labelProvider, doCreateGuiControl, caption);
	}

	@Override
	protected void addElementToGui(int index, T element)
	{
		list.add(labelProvider.getText(element), index);
	}

	@Override
	protected Control createGuiControl(Composite parent, int style, String caption)
	{
		style |= SWT.BORDER;
		if (caption != null) {
			XComposite comp = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, 2);
			label = new Label(comp, SWT.NONE);
			label.setText(caption);
			list = new List(comp, style | SWT.V_SCROLL | SWT.H_SCROLL);
		}
		else {
			list = new List(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
			if (parent.getLayout() instanceof GridLayout)
				list.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fireSelectionChangedEvent();
			}
		});
		return list;
	}
	
	public Label getLabel() {
		if (label != null)
			return label;
		else
			throw new IllegalStateException("Cannot access the label if its creation hasn't been requested earlier.");
	}

	@Override
	protected int getSelectionIndex()
	{
		return list.getSelectionIndex();
	}

	@Override
	protected int[] getSelectionIndices()
	{
		return list.getSelectionIndices();
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

	@Override
	protected int getSelectionCount()
	{
		return list.getSelectionCount();
	}

	@Override
	protected void setSelection(int[] indices)
	{
		list.select(indices);
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
		list.setItem(index, labelProvider.getText(elem));
	}	
}