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
	 * @param layoutMode the layoutMode to set
	 * @param layoutDataMode the layoutDataMode to set
	 */
	public ComboComposite(List<T> types, ILabelProvider labelProvider, Composite parent, 
			int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.types = types;
		this.labelProvider = labelProvider;	
		populateCombo();
	}
	
//	/**
//	 * 
//	 * @param types a List of the generic types which should be selected in the combo
//	 * @param typeDescriptors a optional List of the String which will be displayed in the combo
//	 * @param parent the parent Composite
//	 * @param style the SWT style flag
//	 */
//	public ComboComposite(List<T> types, List<String> typeDescriptors, 
//			Composite parent, int style)
//	{
//		this(types, typeDescriptors, null, parent, style);
//	}
//	
//	/**
//	 * @param types a List of the generic types which should be selected in the combo
//	 * @param typeDescriptors a optional List of the String which will be displayed in the combo
//	 * @param typeImages a optional List of Images which will be displayed in the combo
//	 * @param parent the parent Composite
//	 * @param style the SWT style flag
//	 */
//	public ComboComposite(List<T> types, List<String> typeDescriptors, List<Image> typeImages, 
//			Composite parent, int style) 
//	{
//		super(parent, style);
//		this.types = types;
//		this.typeDescriptors = typeDescriptors;
//		this.typeImages = typeImages;
//		setLayout(getLayout(LayoutMode.TIGHT_WRAPPER));
//		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		populateCombo();
//	}
//
//	/**
//	 * @param types a List of the generic types which should be selected in the combo
//	 * @param typeDescriptors a optional List of the String which will be displayed in the combo
//	 * @param typeImages a optional List of Images which will be displayed in the combo	 * 
//	 * @param parent the parent Composite
//	 * @param style the SWT style flag
//	 * @param layoutMode the layoutMode
//	 * @param layoutDataMode the layoutDataMode
//	 */
//	public ComboComposite(List<T> types, List<String> typeDescriptors, List<Image> typeImages, 
//			Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
//	{
//		super(parent, style, layoutMode, layoutDataMode);
//		this.types = types;
//		this.typeDescriptors = typeDescriptors;
//		this.typeImages = typeImages;
//		populateCombo();
//	}
		
	private ILabelProvider labelProvider = new LabelProvider();
	private List<T> types = null;
//	private List<String> typeDescriptors = null;
//	private List<Image> typeImages = null;		
//	private Combo combo = null;
	private ColorCombo imageCombo = null;	
	
	protected void populateCombo() 
	{
		imageCombo = new ColorCombo(this, SWT.BORDER | SWT.READ_ONLY);
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
	
//	protected void populateCombo() 
//	{
//		// no images use normal Combo
//		if (typeImages == null) 
//		{
//			combo = new Combo(this, SWT.READ_ONLY);
//			combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//			if (typeDescriptors == null) {
//				for (T type : types) {
//					combo.add(type.toString());
//				}
//			}
//			else if (typeDescriptors != null) {
//				for (String description : typeDescriptors) {
//					combo.add(description);
//				}
//			}
//		}
//		// use ColorCombo to display additional images		
//		else 
//		{
//			imageCombo = new ColorCombo(this, SWT.READ_ONLY);
//			imageCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//			for (int i=0; i<typeImages.size(); i++) 
//			{
//				if (typeDescriptors == null) {
//					imageCombo.add(typeImages.get(i), types.get(i).toString());
//				}
//				else if (typeDescriptors != null) {
//					imageCombo.add(typeImages.get(i), typeDescriptors.get(i));					
//				}							
//			}
//		}
//	}
//	
//	public T getSelectedElement() 
//	{
//		int selectionIndex = -1;
//		if (combo != null) {
//			selectionIndex = combo.getSelectionIndex();
//		}
//		if (imageCombo != null) {
//			selectionIndex = imageCombo.getSelectionIndex();
//		}
//		if (selectionIndex != -1)
//			return types.get(selectionIndex);
//		return null;			
//	}
//		
//	public boolean selectElement(T element) 
//	{
//		if (combo != null) 
//		{
//			int index = types.indexOf(element);
//			if (index != -1) {
//				combo.select(index);
//				return true;
//			}					
//		}
//		else if (imageCombo != null)
//		{
//			int index = types.indexOf(element);
//			if (index != -1) {
//				imageCombo.select(index);
//				return true;
//			}								
//		}
//		return false;
//	}
	
}
