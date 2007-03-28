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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.action.AbstractContributionItem;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.notification.SelectionManager;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.base.resource.SharedImages.ImageDimension;
import org.nightlabs.base.resource.SharedImages.ImageFormat;
import org.nightlabs.notification.NotificationEvent;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SearchContributionItem 
extends AbstractContributionItem 
{

	@Override
	protected Control createControl(Composite parent) {
		Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		comp.setLayout(layout);
		
		searchText = new Text(comp, SWT.BORDER);
//		searchText.setSize(150, SWT.DEFAULT);
		GridData textData = new GridData(150, 15);
		textData.minimumWidth = 100;		
		searchText.setLayoutData(textData);
				
		searchButton = new Button(comp, SWT.BOTTOM | SWT.PUSH);
//		Point buttonSize = searchButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//		GridData buttonData = new GridData(buttonSize.x, buttonSize.y);
//		buttonData.minimumWidth = buttonSize.x;
//		searchButton.setLayoutData(buttonData);
		
//		searchButton.setText(TradePlugin.getResourceString("ProductTypeSearchContributionItem.searchButton.label"));
		searchButton.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
				SearchContributionItem.class, "", ImageDimension._12x12, ImageFormat.png));		
		searchText.addSelectionListener(buttonSelectionListener);
		searchButton.addSelectionListener(buttonSelectionListener);
		
//		Menu menu = new Menu(searchButton);
//		menu.setVisible(true);
//		menu.setEnabled(true);
//		searchTypes = new ArrayList<String>(SearchResultProviderRegistry.sharedInstance().getRegisteredNames());
//		for (String type : searchTypes) {
//			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
//			menuItem.setText(type);
//			menuItem.addSelectionListener(new SelectionListener(){			
//				public void widgetSelected(SelectionEvent e) {
//					selectedType = ((MenuItem) e.getSource()).getText();
//					searchPressed();
//				}			
//				public void widgetDefaultSelected(SelectionEvent e) {
//					widgetSelected(e);
//				}			
//			});
//		}
//		searchButton.setMenu(menu);
//		selectedType = searchTypes.get(0);
		
		searchTypeCombo = new Combo(comp, SWT.NONE);
		fillSearchTypeCombo();
		searchTypeCombo.addSelectionListener(comboSelectionListener);
		
		return comp;
	}

	private Text searchText;
	private Button searchButton;	
	private List<String> searchTypes = null;
	
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
			 
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};	
	protected String getSelectedType() {
		return searchTypes.get(searchTypeCombo.getSelectionIndex());
	}
	
//	private String selectedType = null;
//	protected String getSelectedType() {
//		return selectedType;
//	}
		
	private void searchPressed() 
	{
		String selectedType = getSelectedType();
		ISearchResultProvider searchResultProvider = SearchResultProviderRegistry.
			sharedInstance().getSearchResultProvider(selectedType);
		if (searchResultProvider != null) { 
			searchResultProvider.setSearchText(searchText.getText());
			Collection selectedObjects = searchResultProvider.getSelectedObjects();
			if (selectedObjects != null) {
				SelectionManager.sharedInstance().notify(new NotificationEvent(
						SearchContributionItem.this, searchResultProvider.getSelectionZone(), 
						selectedObjects, searchResultProvider.getResultTypeClass()));
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

}
