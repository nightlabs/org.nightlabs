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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.custom.XCombo;

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
	public CComboComposite(List<T> types, Composite parent, int style, String caption)
	{
		this(types, new LabelProvider(), parent, style, caption);
	}

	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param labelProvider the labelProvider
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 */
	public CComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, int style, String caption)
	{
		this(types, labelProvider, parent, style, caption, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
	}

	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param labelProvider the labelProvider
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param comboStyle the SWT style flag of the combo 
	 */
	public CComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, int style, String caption, int comboStyle)
	{
		this(types, labelProvider, parent, style, caption, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, comboStyle);
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
			int style, String caption, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		this(types, labelProvider, parent, style, caption, layoutMode, layoutDataMode, XComposite.getBorderStyle(parent) | SWT.READ_ONLY);
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
			int style, String text, LayoutMode layoutMode, LayoutDataMode layoutDataMode, int comboStyle)
	{
		super(parent, style, layoutMode, layoutDataMode);
		if (types == null)
			throw new IllegalArgumentException("param types must not be null!");

		wrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		wrapper.getGridLayout().verticalSpacing = 5;
		
		this.types = types;
		
		if ( text != null && ! "".equals(text) ) {
			label = new Label(wrapper, SWT.NONE);
			setCaption(text);
		}
		
		imageCombo = new XCombo(wrapper, comboStyle);
		imageCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (labelProvider == null)
			this.labelProvider = new LabelProvider();
		else
			this.labelProvider = labelProvider;
		populateCombo();
	}
	
	private ILabelProvider labelProvider = null;
	private List<T> types = null;
	private XComposite wrapper = null;
	private Label label = null;
	private XCombo imageCombo = null;	
	
	public void setCaption(String text) {
		if (label == null)
			return;
		
		if (text == null)
			text = "";
		
		label.setText(text);
	}
	
	public String getTitle() {
		return label == null ? null : label.getText(); 
	}
	
	protected void populateCombo() 
	{
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
	
	public int getSelectionIndex()
	{
		return imageCombo.getSelectionIndex();
	}
	
	public void setSelection(int index)
	{
		imageCombo.select(index);
	}
	
	public void setSelection(T element)
	{
		int index = types.indexOf(element);
		if (index == -1)
			return;

		imageCombo.select(index);
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

	public List<T> getTypes() {
		return types;
	}
	
	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.XCombo#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener listener) {
		imageCombo.addModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.XCombo#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void addSelectionListener(SelectionListener listener) {
		imageCombo.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.XCombo#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(ModifyListener listener) {
		imageCombo.removeModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.XCombo#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener) {
		imageCombo.removeSelectionListener(listener);
	}

	/**
	 * @param index
	 * @return
	 * @see org.nightlabs.base.custom.XCombo#getItem(int)
	 */
	public TableItem getItem(int index) {
		return imageCombo.getItem(index);
	}

	/**
	 * @return
	 * @see org.nightlabs.base.custom.XCombo#getItemCount()
	 */
	public int getItemCount() {
		return imageCombo.getItemCount();
	}

	/**
	 * @param index
	 * @see org.nightlabs.base.custom.XCombo#select(int)
	 */
	public void select(int index) {
		imageCombo.select(index);
	}
	
	/**
	 * Convienience method for {@link #setItems(List)}.
	 */
	public void setInput(List<T> types) {
		setItems(types);
	}
			
	public void setItems(List<T> types) {
		imageCombo.removeAll();
		this.types = types;
		for (T type : types) {
			imageCombo.add(labelProvider.getImage(type), labelProvider.getText(type));
		}
	}

	/**
	 * @return the backend XCombo widget.
	 */
//	public XCombo getCombo() {
//		return imageCombo;
//	}
	
}
