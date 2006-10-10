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

package org.nightlabs.base.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.composite.XComposite;


/**
 * An base Composite for all Table composites
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class AbstractTableComposite<ElementType>
extends XComposite
implements ISelectionProvider
{

	public static int DEFAULT_STYLE_SINGLE = SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE;
	public static int DEFAULT_STYLE_MULTI = SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI;
	
	protected TableViewer tableViewer;
	protected Table table;

	public AbstractTableComposite(Composite parent, int style) {
		this(parent, style, true);
	}
	
	public AbstractTableComposite(Composite parent, int style, boolean initTable) {
		this(parent, style, initTable, DEFAULT_STYLE_MULTI);
	}
	
	public AbstractTableComposite(Composite parent, int style, boolean initTable, int viewerStyle) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
//		GridLayout thisLayout = new GridLayout();
//		this.setLayout(thisLayout);
//
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		this.setLayoutData(gd);
		
		tableViewer = new TableViewer(this, viewerStyle);		
		GridData tgd = new GridData(GridData.FILL_BOTH);
		table = tableViewer.getTable(); 
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(tgd);		
		table.setLayout(new TableLayout());

		init();
		if (initTable)
			initTable();
	}
	
	protected void initTable() {
		createTableColumns(tableViewer, table);
		setTableProvider(tableViewer);
	}
	
	/**
	 * Calls refresh for the TableViewer.
	 */
	public void refresh() {
		tableViewer.refresh();		
	}
	
	public void refresh(boolean updateLabels) {
		tableViewer.refresh(updateLabels);
	}
	
	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	/**
	 * Override for initializatioin to be done
	 * before {@link #createTableColumns(TableViewer, Table)} and {@link #setTableProvider(TableViewer)}.
	 * Default implementation does nothing.
	 */
	public void init() {}
	
	
	/**
	 * Return the table viewer's selection in a Collection
	 * of the element types of this table.  
	 * @return the table viewer's selection
	 */
	public Collection<ElementType> getSelectedElements() {
		ISelection sel = getTableViewer().getSelection();
		if (sel == null || sel.isEmpty())
			return Collections.EMPTY_LIST;
		else if (sel instanceof IStructuredSelection) {
			Collection<ElementType> result = new ArrayList<ElementType>();
			IStructuredSelection selection = (IStructuredSelection) sel;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = (Object) iter.next();
				result.add((ElementType)obj);
			}
			return result;
		}
		else
			return Collections.EMPTY_LIST;
	}
	
	/**
	 * The first selected element. Or <code>null</code> if none selected.
	 * <p>
	 * Note that this method will cast the selection found
	 * to the ElementType this table composite was typed with.
	 * If the selected element is not of this type a
	 * {@link ClassCastException} will be thrown.
	 * 
	 * @return The first selected element.
	 */
	public ElementType getFirstSelectedElement() {
		return (ElementType) getFirstSelectedElementUnchecked();
	}

	/**
	 * Returns the first selected element without casting
	 * it to the ElementType this table composite was
	 * typed with.
	 * 
	 * @return The first selected element (of any type).
	 */
	public Object getFirstSelectedElementUnchecked() {
		ISelection sel = getTableViewer().getSelection();
		if (sel == null || sel.isEmpty())
			return null;
		else if (sel instanceof IStructuredSelection)
			return ((IStructuredSelection)sel).getFirstElement();
		else
			return null;
	}
	
	/**
	 * Add your columns here to the Table.
	 * @param tableViewer The TableViewer.
	 * @param table A shortcut to <code>tableViewer.getTable()</code>.
	 */
	protected abstract void createTableColumns(TableViewer tableViewer, Table table);

	/**
	 * Set your content and label provider for the tableViewer.
	 * 
	 * @param tableViewer The TableViewer.
	 */
	protected abstract void setTableProvider(TableViewer tableViewer);

	public Table getTable() {
		return table;
	}

	/**
	 * Sets the tableViewers input.
	 *
	 */
	public void setInput(Object input) {
		if (tableViewer != null)
			tableViewer.setInput(input);
	}

	/**
	 * Sets the selection to the given list of elements.
	 * TODO: No need for this method?
	 *  
	 * @param elements The elements to select.
	 */
	public void setSelectedEelements(List<ElementType> elements) {
		TableItem[] items = tableViewer.getTable().getItems();
		List<Integer> selIndexes = new ArrayList<Integer>();
		for (int i = 0; i < items.length; i++) {
			if (elements.contains(items[i].getData()))
				selIndexes.add(i);
		}
		int[] selection = new int[selIndexes.size()];
		for (int i = 0; i < selection.length; i++) {
			selection[i] = selIndexes.get(i);
		}
		tableViewer.getTable().select(selection);
	}

	/**
	 * If the this table-composite's table was created with
	 * the {@link SWT#CHECK} flag this method will
	 * exlusively check the rows for the given element list.
	 * 
	 * @param elements The element to check.
	 */
	public void setCheckedEelements(List<ElementType> elements) {
		if ((table.getStyle() & SWT.CHECK) == 0)
			return;
		TableItem[] items = tableViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setChecked(false);
			if (elements.contains(items[i].getData()))
				items[i].setChecked(true);
		}
	}
	
	/**
	 * Set the viewers selection.
	 * 
	 * @param elements The selection to set
	 * @see TableViewer#setSelection(ISelection)
	 */
	public void setSelection(List<ElementType> elements) {
		getTableViewer().setSelection(new StructuredSelection(elements));
	}

	/**
	 * Set the viewers selection.
	 * 
	 * @param selection The selection to set.
	 * @param reveal If true the selection will be made visible 
	 * @see TableViewer#setSelection(ISelection, boolean)
	 */
	public void setSelection(List<ElementType> elements, boolean reveal) {
		getTableViewer().setSelection(new StructuredSelection(elements), reveal);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		tableViewer.addSelectionChangedListener(listener);
	}

	public ISelection getSelection()
	{
		return tableViewer.getSelection();
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		tableViewer.removeSelectionChangedListener(listener);
	}

	public void setSelection(ISelection selection)
	{
		tableViewer.setSelection(selection);
	}
}
