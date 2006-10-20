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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ComboComposite<T> extends AbstractListComposite<T> {
	private Label label;

	private Combo combo;

	public ComboComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}

	public ComboComposite(Composite parent, int style) {
		super(parent, style);
	}

	public ComboComposite(Composite parent, int style, List<T> elements) {
		super(parent, style);
		addElements(elements);
	}

	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode) {
		super(labelProvider, parent, style, layoutMode, layoutDataMode);
	}

	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider) {
		super(labelProvider, parent, style);
	}

	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider, boolean createLabel) {
		super(labelProvider, parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, false, createLabel);
		this.comboStyle = SWT.READ_ONLY;
		createGuiControl(this, SWT.BORDER, createLabel);
		getLabel().setText("Current struct:");
		getLabel().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private int comboStyle = SWT.DROP_DOWN;

	public ComboComposite(Composite parent, List<T> elements, ILabelProvider labelProvider, int comboStyle) {
		super(labelProvider, parent, SWT.NONE, false);
		this.comboStyle |= comboStyle;
		createGuiControl(this, SWT.BORDER, false);
		addElements(elements);
	}

	public ComboComposite(Composite parent, ILabelProvider labelProvider, int comboStyle) {
		super(labelProvider, parent, SWT.NONE, false);
		this.comboStyle |= comboStyle;
		createGuiControl(this, SWT.BORDER, false);
	}

	@Override
	protected void addElementToGui(int index, T element) {
		combo.add(labelProvider.getText(element), index);
	}

	@Override
	protected Control createGuiControl(Composite parent, int style, boolean createLabel) {
		if (createLabel) {
			XComposite comp = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, 2);
			label = new Label(comp, SWT.NONE);
			combo = new Combo(comp, style | comboStyle);
		} else {
			combo = new Combo(parent, style | comboStyle);
			if (parent.getLayout() instanceof GridLayout) {
				combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			}
		}
		return combo;
	}

	public Label getLabel() {
		if (label != null)
			return label;
		else
			throw new IllegalStateException("Cannot access the label if its creation hasn't been requested earlier.");
	}

	@Override
	protected int getSelectionIndex() {
		return combo.getSelectionIndex();
	}

	@Override
	protected int[] getSelectionIndices() {
		return new int[] { combo.getSelectionIndex() };
	}

	@Override
	protected void removeAllElementsFromGui() {
		combo.removeAll();
	}

	@Override
	protected void removeElementFromGui(int index) {
		combo.remove(index);
	}

	@Override
	protected void setSelection(int index) {
		combo.select(index);
	}

	@Override
	protected void setSelection(int[] indices) {
		if (indices.length < 1)
			combo.select(-1); // seems to be ignored - a combo seems to have always
		// one item selected
		else
			combo.select(indices[0]);
	}

	@Override
	protected int getSelectionCount() {
		if (combo.getSelectionIndex() < 0 || combo.getItemCount() < 1)
			return 0;
		else
			return 1;
	}

	public Combo getCombo() {
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
	protected void refreshElement(T element) {
		int index = getElementIndex(element);
		combo.setItem(index, labelProvider.getText(element));
	}
}