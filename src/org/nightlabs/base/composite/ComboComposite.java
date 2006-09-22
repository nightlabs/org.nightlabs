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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ComboComposite<T> extends AbstractListComposite<T>
{
	private Combo combo;
	
	public ComboComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);
	}

	public ComboComposite(Composite parent, int style)
	{
		super(parent, style);
	}
	
	public ComboComposite(Composite parent, int style, List<T> elements)
	{
		super(parent, style);
		addElements(elements);
	}

	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(labelProvider, parent, style, layoutMode, layoutDataMode);
	}

	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider)
	{
		super(labelProvider, parent, style);
	}
	
	private int comboStyle = SWT.DROP_DOWN;
	
	public ComboComposite(Composite parent, List<T> elements, ILabelProvider labelProvider, int comboStyle) 
	{
		super(labelProvider, parent, SWT.NONE);
		this.comboStyle |= comboStyle;
		addElements(elements);
	}
	
	public ComboComposite(Composite parent, ILabelProvider labelProvider, int comboStyle) 
	{
		super(labelProvider, parent, SWT.NONE);
		this.comboStyle |= comboStyle;
	}
	
	@Override
	protected void addElementToGui(T element)
	{
		combo.add(labelProvider.getText(element));
	}

	@Override
	protected Control createGuiControl(Composite parent, int style)
	{
		combo = new Combo(parent, style | comboStyle);		
		return combo;
	}

	@Override
	protected int getSelectedIndex()
	{
		return combo.getSelectionIndex();
	}

	@Override
	protected void removeAllElementsFromGui()
	{
		combo.removeAll();
	}

	@Override
	protected void removeElementFromGui(int index)
	{
		combo.remove(index);
	}

	@Override
	protected void setSelection(int index)
	{
		combo.select(index);
	}
	
	public Combo getCombo()
	{
		return combo;
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Combo#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener listener) {
		combo.addModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Combo#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void addSelectionListener(SelectionListener listener) {
		combo.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Combo#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(ModifyListener listener) {
		combo.removeModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Combo#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener) {
		combo.removeSelectionListener(listener);
	}

	@Override
	protected void refreshElement(T element)
	{
		int index = getElementIndex(element);
		combo.setItem(index, labelProvider.getText(element));
	}	
}