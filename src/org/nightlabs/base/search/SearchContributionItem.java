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
package org.nightlabs.base.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.action.AbstractContributionItem;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.notification.SelectionManager;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.base.resource.SharedImages.ImageDimension;
import org.nightlabs.base.resource.SharedImages.ImageFormat;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.notification.NotificationEvent;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SearchContributionItem 
extends AbstractContributionItem 
{
	private static final Logger logger = Logger.getLogger(SearchContributionItem.class);
	
	@Override
	protected Control createControl(Composite parent) 
	{
		Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		comp.setLayout(layout);

		createText(comp);
		
		searchButton = new Button(comp, SWT.BOTTOM | SWT.PUSH);
		searchButton.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
				SearchContributionItem.class, "", ImageDimension._12x12, ImageFormat.png));		
		searchButton.addSelectionListener(buttonSelectionListener);
				
		searchTypeCombo = new Combo(comp, SWT.NONE);
		fillSearchTypeCombo();
		searchTypeCombo.addSelectionListener(comboSelectionListener);
		
		return comp;
	}

	protected void createText(Composite parent) 
	{
		searchText = new Text(parent, SWT.BORDER);
//		GridData textData = new GridData(100, 15);
//		textData.minimumWidth = 100;
//		searchText.setLayoutData(textData);		
		searchText.addSelectionListener(buttonSelectionListener);		
	}
	
	private Text searchText;
	private List<String> searchTypes = null;
	
	private Button searchButton;		
	private Combo searchTypeCombo;
	private void fillSearchTypeCombo() 
	{
		searchTypes = new ArrayList<String>(SearchResultProviderRegistry.sharedInstance().getRegisteredNames());
		searchTypeCombo.setItems(searchTypes.toArray(new String[searchTypes.size()]));
		if (searchTypeCombo.getItemCount() > 0)
			searchTypeCombo.select(0);
	}	
	
	private SelectionListener comboSelectionListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			selectedType = searchTypes.get(searchTypeCombo.getSelectionIndex());
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};
		
	private String selectedType = null;
	protected String getSelectedType() {
		return selectedType;
	}
		
	private void searchPressed() 
	{
		String selectedType = getSelectedType();
		ISearchResultProvider searchResultProvider = SearchResultProviderRegistry.
			sharedInstance().getSearchResultProvider(selectedType);
		if (searchResultProvider != null) { 
			searchResultProvider.setSearchText(searchText.getText());
			Collection selectedObjects = searchResultProvider.getSelectedObjects();
			Collection<Class> subjectClassesToClear = new ArrayList<Class>();
			subjectClassesToClear.add(searchResultProvider.getResultTypeClass());
			if (selectedObjects != null) {
				SelectionManager.sharedInstance().notify(new NotificationEvent(
						SearchContributionItem.this, searchResultProvider.getSelectionZone(), 
						selectedObjects, subjectClassesToClear));
			}
		}		
	}
	
	private SelectionListener buttonSelectionListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) {
			searchPressed();
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};	

	protected Menu createMenu() 
	{
		Menu menu = new Menu(RCPUtil.getActiveWorkbenchShell(), SWT.POP_UP);
		searchTypes = new ArrayList<String>(SearchResultProviderRegistry.sharedInstance().getRegisteredNames());
		for (String type : searchTypes) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(type);
			menuItem.addSelectionListener(new SelectionListener(){			
				public void widgetSelected(SelectionEvent e) {
					selectedType = ((MenuItem) e.getSource()).getText();
					searchPressed();
				}			
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}			
			});
		}
		selectedType = searchTypes.get(0);		
		return menu;
	}
	
	public void fill(ToolBar parent, int index) 
	{
		if (fillToolBar) 
		{
			toolItem = new ToolItem(parent, SWT.SEPARATOR, index);			
	  	createText(parent);
	  	toolItem.setControl(searchText);
	  	toolItem.setWidth(100);			
	  	
			final ToolBar toolBar = parent;
	  	final Menu menu = createMenu();
	  	final ToolItem searchItem = new ToolItem(parent, SWT.DROP_DOWN);
	  	searchItem.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
					SearchContributionItem.class));
	  	searchItem.addListener(SWT.Selection, new Listener(){	
	  		public void handleEvent(Event event) {
	  			if (event.detail == SWT.ARROW) {
	  				Rectangle rect = searchItem.getBounds();
	  				Point p = new Point(rect.x, rect.y + rect.height);
	  				p = toolBar.toDisplay(p);
	  				menu.setLocation(p.x, p.y);
	  				menu.setVisible(true);
	  			}
	  			if (event.detail == SWT.NONE) {
	  				searchPressed();
	  			}
	  		}
	  	});
	  	toolBar.layout(true, true);	 	  	
		}
	}	
		
	@Override
	public void fill(CoolBar parent, int index) 
	{
		if (fillCoolBar) 
		{
			final CoolBar coolBar = parent;
			final ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT | SWT.WRAP);			
	  	final Menu menu = createMenu();
	  	
	  	toolItem = new ToolItem(toolBar, SWT.SEPARATOR);			
	  	createText(toolBar);
	  	toolItem.setControl(searchText);
	  	toolItem.setWidth(100);
	  	
	  	final ToolItem searchItem = new ToolItem(toolBar, SWT.DROP_DOWN);	  	
	  	searchItem.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
					SearchContributionItem.class));
	  	searchItem.addListener(SWT.Selection, new Listener(){	
	  		public void handleEvent(Event event) 
	  		{
	  			logger.info("event.detail = "+event.detail);
	  			if (event.detail == SWT.ARROW) {
	  				Rectangle rect = searchItem.getBounds();
	  				Point p = new Point(rect.x, rect.y + rect.height);
	  				p = toolBar.toDisplay(p);
	  				menu.setLocation(p.x, p.y);
	  				menu.setVisible(true);
	  			}
	  			if (event.detail == SWT.NONE) {
	  				searchPressed();
	  			}	  			
	  		}
	  	});	  	
	  	toolBar.layout(true, true);
	  	
	  	CoolItem coolItem = new CoolItem(coolBar, SWT.SEPARATOR);
	  	coolItem.setControl(toolBar);
	  	Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);	  	
	  	Point coolSize = coolItem.computeSize(size.x, size.y);
	  	coolItem.setMinimumSize(coolSize.x - 10, coolSize.y);
//	  	coolItem.setPreferredSize(coolSize.x, coolSize.y);
	  	coolItem.setSize(coolSize.x - 10, coolSize.y);	  	
//	  	logger.info("size = "+size);
		}
	}
		
}
