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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

//	public ComboComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
//		super(parent, style, layoutMode, layoutDataMode);
//	}

	public ComboComposite(Composite parent, int style)
	{
		super(parent, style);
	}

	public ComboComposite(Composite parent, int style, List<T> elements) {
		this(parent, style, elements, (String)null);
	}

	public ComboComposite(Composite parent, int style, List<T> elements, String caption) {
		super(parent, style,  new LabelProvider(), false, caption);
		addElements(elements);
		createGuiControl(parent, style, caption);
	}

//	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider, LayoutMode layoutMode,
//			LayoutDataMode layoutDataMode) {
//		super(labelProvider, parent, style, layoutMode, layoutDataMode);
//	}
//
//	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider) {
//		super(labelProvider, parent, style);
//	}

	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider, String caption) {
		super(parent, style, labelProvider, false, caption);
//		this.comboStyle = SWT.READ_ONLY;
		createGuiControl(this, SWT.BORDER, caption);
	}


//	private int comboStyle = SWT.DROP_DOWN | SWT.READ_ONLY;
	public ComboComposite(Composite parent, int style, List<T> elements, ILabelProvider labelProvider) {
		this(parent, style, elements, labelProvider, (String)null);
	}

	public ComboComposite(Composite parent, int style, String caption) {
		super(parent, style,  new LabelProvider(), true, caption);
	}

	public ComboComposite(Composite parent, int style, List<T> elements, ILabelProvider labelProvider, String caption) {
//		super(labelProvider, parent, SWT.NONE, false);
		super(parent, labelProvider, false, null);
//		this.comboStyle = comboStyle;
		createGuiControl(this, style, caption);
		addElements(elements);
	}

	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider) {
		super(parent, labelProvider, false, null);
		createGuiControl(this, style, null);
	}

	protected void addElementToGui(int index, T element) {
		combo.add(labelProvider.getText(element), index);
	}

	protected Control createGuiControl(Composite parent, int style, String caption) {
//		this.getGridData().grabExcessVerticalSpace = false;
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		style |= SWT.BORDER;
		if ((style & SWT.SIMPLE) == 0 && (style & SWT.DROP_DOWN) == 0)
			style |= SWT.DROP_DOWN;
		if (caption != null) {
			XComposite comp = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA, 2);
			comp.getGridData().grabExcessVerticalSpace = false;
			label = new Label(comp, SWT.NONE);
			label.setText(caption);
			combo = new Combo(comp, style);
		} else {
			combo = new Combo(parent, style);
			if (parent.getLayout() instanceof GridLayout) {
				combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			}
		}
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fireSelectionChangedEvent();
			}
		});
		return combo;
	}

	public Label getLabel() {
		if (label != null)
			return label;
		else
			throw new IllegalStateException("Cannot access the label if its creation hasn't been requested earlier.");
	}

	protected int internal_getSelectionIndex() {
		return combo.getSelectionIndex();
	}

	protected int[] internal_getSelectionIndices() {
		return new int[] { combo.getSelectionIndex() };
	}

	protected void removeAllElementsFromGui() {
		combo.removeAll();
	}

	protected void removeElementFromGui(int index) {
		combo.remove(index);
	}

	protected void internal_setSelection(int index) {
		combo.select(index);
	}

	protected void internal_setSelection(int[] indices) {
		if (indices.length < 1)
			combo.select(-1); // seems to be ignored - a combo seems to have always
		// one item selected
		else
			combo.select(indices[0]);
	}

	protected int internal_getSelectionCount() {
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
	 * @deprecated The methods from combo should be completely hidden - there is already an ISelectionProvider implemented in this class
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
	 * @deprecated The methods from combo should be completely hidden - there is already an ISelectionProvider implemented in this class
	 */
	public void removeSelectionListener(SelectionListener listener) {
		combo.removeSelectionListener(listener);
	}

	protected void refreshElement(T element) {
		int index = getElementIndex(element);
		combo.setItem(index, labelProvider.getText(element));
	}

	/**
	 * @return the result from the delegated {@link Combo#getText()}.
	 */
	public String getText()
	{
		return combo.getText();
	}
	/**
	 * This method delegates to {@link Combo#setText(String)}
	 * @param text The text.
	 */
	public void setText(String text)
	{
		combo.setText(text);
	}
}