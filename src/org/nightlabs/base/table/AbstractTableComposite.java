/*
 * Created 	on Mar 9, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.table;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import org.nightlabs.base.composite.XComposite;
;

/**
 * An base Composite for all Table composites
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
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
		createTableColumns(table);
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
	 * before {@link #createTableColumns(Table)} and {@link #setTableProvider(TableViewer)}.
	 * Default implementation does nothing.
	 */
	public void init() {}
	
	/**
	 * Add your columns here to the Table.
	 * 
	 * @param table
	 */
	protected abstract void createTableColumns(Table table);
	
	/**
	 * Set your content and label provider for the tableViewer.
	 * 
	 * @param tableViewer
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
