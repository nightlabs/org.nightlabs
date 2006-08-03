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
import org.nightlabs.base.custom.ColorCombo;

/**
 * @author Daniel.Mazurek at Nightlabs dot de
 *
 */
public class ComboComposite<T> 
extends XComposite 
{
	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 */
	public ComboComposite(List<T> types, Composite parent, int style)
	{
		this(types, null, parent, style);
	}

	/**
	 * 
	 * @param types a List of the generic types which should be selected in the combo
	 * @param labelProvider the labelProvider
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 */
	public ComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, int style)
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
	public ComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, int style, int comboStyle)
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
	public ComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, 
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
	public ComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, 
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
		
}
