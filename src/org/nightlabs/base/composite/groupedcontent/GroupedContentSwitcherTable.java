/*
 * Created 	on Jun 17, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.composite.groupedcontent;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.nightlabs.base.table.AbstractTableComposite;
import org.nightlabs.base.table.TableContentProvider;
import org.nightlabs.base.table.TableLabelProvider;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class GroupedContentSwitcherTable extends AbstractTableComposite {
	
	private String groupTitle;
	private TableColumn column;
	
	// TODO: dispose sometime
	private static final Color COLOR_HIGHLIGHT = new Color(null, 200, 200, 200);
	private static final Color COLOR_SELECTED = new Color(null, 11, 5, 180);
	private static final Color COLOR_BACKGROUND = new Color(null, 111, 111, 111);

	private MouseMoveListener mouseMoveListener = new MouseMoveListener() {
		public void mouseMove(MouseEvent event) {
			TableItem item = getTableItem(event);
			highLightItem(item);
		}
		protected TableItem getTableItem(MouseEvent event)
		{
			Point pt = new Point(event.x, event.y);
			TableItem tableItem = getTable().getItem(pt);
			return tableItem;
		}
	};
	
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			int idx = getTable().getSelectionIndex();			
			if (idx >= 0)
				selectItem(getTable().getItem(idx));
		}
	};
	
	private class ContentProvider extends TableContentProvider  {
		/**
		 * @see org.nightlabs.base.table.TableContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection)
				return ((Collection)inputElement).toArray();
			return super.getElements(inputElement);
		}
	}
	
	private class LabelProvider extends TableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof GroupedContentProvider)
				return ((GroupedContentProvider)element).getGroupIcon();
			return super.getColumnImage(element, columnIndex);
		}
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof GroupedContentProvider)
				return ((GroupedContentProvider)element).getGroupTitle();
			throw new IllegalArgumentException("LabelProvider for GroupedContentSwitcherTable is restricted to GroupedContentProvider elements.");
		}
	}	
	
	public GroupedContentSwitcherTable(Composite parent, int style) {
		super(parent, style, true, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		getTable().setLinesVisible(false);
		getTable().setHeaderVisible(false);
		getTable().setBackground(COLOR_BACKGROUND);
		getTable().addMouseMoveListener(mouseMoveListener);
		getTableViewer().addSelectionChangedListener(selectionListener);
//		getTable().setForeground(parent.getForeground());
	}

	protected void createTableColumns(Table table) {
//		column = new TableColumn(table, SWT.LEFT); 
//		if (groupTitle != null)
//			column.setText(groupTitle);
//		table.setLayout(new WeightedTableLayout(new int[] {1}));		
	}

	protected void setTableProvider(TableViewer tableViewer) {		
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

	/**
	 * @return Returns the groupTitle.
	 */
	public String getGroupTitle() {
		return groupTitle;
	}

	/**
	 * @param groupTitle The groupTitle to set.
	 */
	public void setGroupTitle(String groupTitle) {			
		this.groupTitle = groupTitle;
//		if (groupTitle != null){
//			column.setText(groupTitle);
//			layout();
//		}			
	}
	
	protected void selectItem(TableItem item) {
		TableItem[] items = getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].setBackground(COLOR_BACKGROUND);
		}
		if (item != null)
			item.setBackground(COLOR_SELECTED);
	}
	
	protected void highLightItem(TableItem item) {
		TableItem[] items = getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getBackground() != COLOR_SELECTED)
				items[i].setBackground(COLOR_BACKGROUND);
		}
		if (item != null)
			item.setBackground(COLOR_HIGHLIGHT);
	}
}
