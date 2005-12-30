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

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import org.nightlabs.base.composite.XComposite;


/**
 * An base Composite for all Table composites
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public abstract class AbstractTableComposite extends XComposite {

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
		super(parent, style, XComposite.LAYOUT_MODE_TIGHT_WRAPPER);
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
	 * Add your columns here to the Table.
	 * @param tableViewer The TableViewer.
	 * @param table A shortcut to <code>tableViewer.getTable()</code>.
	 */
	protected abstract void createTableColumns(TableViewer tableViewer, Table table);

	/**
	 * Set your content and label provider for the tableViewer.
	 * 
	 * @param tableViewer The TableViewer.
	 * @param table A shortcut to <code>tableViewer.getTable()</code>.
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
	

}
