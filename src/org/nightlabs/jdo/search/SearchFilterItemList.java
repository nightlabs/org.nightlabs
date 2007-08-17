/* *****************************************************************************
 * org.nightlabs.jdo - NightLabs Eclipse utilities for JDO                     *
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

package org.nightlabs.jdo.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.composite.XComposite.LayoutMode;


/**
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class SearchFilterItemList 
	extends ScrolledComposite {
	
	private XComposite listWrapper;
	private List<SearchFilterItemEditor> searchFilterItemEditors = new ArrayList<SearchFilterItemEditor>();
	
	public SearchFilterItemList(Composite parent, int style) {
		super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
		GridLayout thisLayout = new GridLayout();
//		thisLayout.numColumns = 3;
//		thisLayout.makeColumnsEqualWidth = true;
		this.setLayout(thisLayout);
		createListWrapperAndLayout();
	}
	
	protected void createListWrapperAndLayout() {
		listWrapper = new XComposite(this, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);
		GridData listWrapperLData = new GridData();
		listWrapperLData.grabExcessHorizontalSpace = true;
		listWrapperLData.horizontalAlignment = SWT.FILL;		
		
		listWrapper.setLayoutData(listWrapperLData);
		setExpandHorizontal(true);
    setExpandVertical(true);		
		setContent(listWrapper);
		setMinSize(listWrapper.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.layout();
	}
	
	/**
	 * Composite used to remove itemEditors.
	 */
	private class SearchFilterItemEditorCloser 
	extends XComposite 
	implements SelectionListener {
		
		private Button buttonClose;

		private SearchFilterItemEditor editor;
		private SearchFilterItemList itemList;
		
		public SearchFilterItemEditorCloser(Composite parent, int style, SearchFilterItemList itemList, SearchFilterItemEditor editor) {
			super(parent, style, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
			GridData gd = new GridData();
			gd.horizontalAlignment = SWT.RIGHT;
			gd.widthHint = 30;
			this.editor = editor;
			this.itemList = itemList;
			buttonClose = new Button(this, SWT.FLAT | SWT.NONE);
			buttonClose.setText("x"); //$NON-NLS-1$
			buttonClose.addSelectionListener(this);
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent evt) {
			itemList.removeItemEditor(editor);
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent evt) {
		}
	}
	
	
	/**
	 * Runnable to add items on the gui thread.
	 * 
	 */
	private class ItemEditorAdder implements Runnable {
		private SearchFilterItemEditor itemEditor;
		protected ItemEditorAdder(SearchFilterItemEditor itemEditor) {
			this.itemEditor = itemEditor;
		}
		public void run() {
			XComposite wrapper = new XComposite(
					listWrapper, 
					SWT.BORDER, 
					LayoutMode.TIGHT_WRAPPER,
					LayoutDataMode.NONE
				);
			((GridLayout)wrapper.getLayout()).numColumns = 2;
			GridData wrapperLData = new GridData();
			wrapperLData.grabExcessHorizontalSpace = true;
			wrapperLData.horizontalAlignment = SWT.FILL;
			wrapper.setLayoutData(wrapperLData);
			
			itemEditor.getControl(wrapper);
			searchFilterItemEditors.add(itemEditor);
			new SearchFilterItemEditorCloser(wrapper,SWT.NONE,SearchFilterItemList.this,itemEditor);
			SearchFilterItemList.this.setMinSize(listWrapper.computeSize(SWT.DEFAULT,SWT.DEFAULT));
//			listWrapper.setSize(listWrapper.computeSize(SWT.DEFAULT,SWT.DEFAULT));
			listWrapper.layout();
//			SearchFilterItemList.this.layout();
		}
		
	}
	
	/**
	 * Runnable to remove items on the gui thread.
	 * 
	 */
	private class ItemEditorRemover implements Runnable {
		private SearchFilterItemEditor itemEditor;
		protected ItemEditorRemover(SearchFilterItemEditor itemEditor) {
			this.itemEditor = itemEditor;
		}
		public void run() {			
			if (!searchFilterItemEditors.contains(itemEditor))
				return;
			itemEditor.close();
			itemEditor.getControl(null).getParent().dispose();
			searchFilterItemEditors.remove(itemEditor);
			
			SearchFilterItemList.this.setMinSize(listWrapper.computeSize(SWT.DEFAULT,SWT.DEFAULT));
			listWrapper.layout();
		}
		
	}

	/**
	 * Add the given SearchFilterItemEditor to the end of the list;
	 * 
	 * @param itemEditor
	 */
	public void addItemEditor(SearchFilterItemEditor itemEditor) {
		if (itemEditor != null)
			Display.getDefault().asyncExec(new ItemEditorAdder(itemEditor));
	}
	
	public void addItemEditor(Class editorClass) {
		SearchFilterItemEditor itemEditor = null;
		try {
			itemEditor = (SearchFilterItemEditor) editorClass.newInstance();
		} catch (Throwable t) {
			throw new IllegalStateException("Error instatiating new SearchFilterItemEditor from class "+editorClass); //$NON-NLS-1$
		}
		addItemEditor(itemEditor);		
	}
	
	/**
	 * Removes the itemEditor and disposes its control. 
	 * @param itemEditor
	 */
	public void removeItemEditor(SearchFilterItemEditor itemEditor) {
		Display.getDefault().asyncExec(new ItemEditorRemover(itemEditor));
	}
	
	
	/**
	 * Adds all filter items in this list to the passed SearchFilter.
	 * 
	 * @param filter
	 */
	public void addItemsToFilter(SearchFilter filter) {
		for (Iterator iter = searchFilterItemEditors.iterator(); iter.hasNext();) {
			SearchFilterItemEditor itemEditor = (SearchFilterItemEditor) iter.next();
			filter.addSearchFilterItem(itemEditor.getSearchFilterItem());
		}
	}
	
	protected List getSearchFilterItemEditors() {
		return searchFilterItemEditors;
	}
	
	public void clear() {
		searchFilterItemEditors.clear();
		if (listWrapper != null && !listWrapper.isDisposed())
			listWrapper.dispose();
		createListWrapperAndLayout();
	}
}
