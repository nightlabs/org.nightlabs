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
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.custom.ColorCombo;

/**
 * @author Daniel.Mazurek at Nightlabs dot de
 *
 */
public class CComboComposite<T> 
extends XComposite 
{
	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 */
	public CComboComposite(List<T> types, Composite parent, int style)
	{
		this(types, new LabelProvider(), parent, style);
	}

	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param labelProvider the labelProvider
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 */
	public CComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, int style)
	{
		this(types, labelProvider, parent, style, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
	}

	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param labelProvider the labelProvider
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param comboStyle the SWT style flag of the combo 
	 */
	public CComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, int style, int comboStyle)
	{
		this(types, labelProvider, parent, style, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, comboStyle);
	}
	
	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param labelProvider the labelProvider
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutMode the layoutMode to set
	 * @param layoutDataMode the layoutDataMode to set
	 */
	public CComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, 
			int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		this(types, labelProvider, parent, style, layoutMode, layoutDataMode, SWT.BORDER | SWT.READ_ONLY);
	}
	
	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param labelProvider the labelProvider
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutMode the layoutMode to set
	 * @param layoutDataMode the layoutDataMode to set
	 * @param comboStyle the SWT style flag for the combo
	 */
	public CComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, 
			int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode, int comboStyle)
	{
		super(parent, style, layoutMode, layoutDataMode);
		if (types == null)
			throw new IllegalArgumentException("param types must not be null!");
		
		this.comboStyle = comboStyle;
		this.types = types;
		if (labelProvider == null)
			this.labelProvider = new LabelProvider();
		else
			this.labelProvider = labelProvider;
		populateCombo();
	}
	
	private int comboStyle = SWT.BORDER | SWT.READ_ONLY;
	private ILabelProvider labelProvider = null;
	private List<T> types = null;
	private ColorCombo imageCombo = null;	
//	public ColorCombo getCombo() {
//		return imageCombo;
//	}
	
	protected void populateCombo() 
	{
		imageCombo = new ColorCombo(this, comboStyle);
		imageCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (T type : types) {
			imageCombo.add(labelProvider.getImage(type), labelProvider.getText(type));
		}
	}
	
	public T getSelectedElement() 
	{
		int selectionIndex = imageCombo.getSelectionIndex();
		if (selectionIndex != -1)
			return types.get(selectionIndex);
		return null;			
	}
	
	public boolean selectElement(T element) 
	{
		int index = types.indexOf(element);
		if (types.indexOf(element) != -1) {
			imageCombo.select(index);
			return true;
		}								
		return false;
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.ColorCombo#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener listener) {
		imageCombo.addModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.ColorCombo#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void addSelectionListener(SelectionListener listener) {
		imageCombo.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.ColorCombo#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(ModifyListener listener) {
		imageCombo.removeModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.ColorCombo#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener) {
		imageCombo.removeSelectionListener(listener);
	}

	/**
	 * @param index
	 * @return
	 * @see org.nightlabs.base.custom.ColorCombo#getItem(int)
	 */
	public TableItem getItem(int index) {
		return imageCombo.getItem(index);
	}

	/**
	 * @return
	 * @see org.nightlabs.base.custom.ColorCombo#getItemCount()
	 */
	public int getItemCount() {
		return imageCombo.getItemCount();
	}

	/**
	 * @param index
	 * @see org.nightlabs.base.custom.ColorCombo#select(int)
	 */
	public void select(int index) {
		imageCombo.select(index);
	}
		
	
}
