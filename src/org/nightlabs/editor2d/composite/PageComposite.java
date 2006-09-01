/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d.composite;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.form.XFormToolkit;
import org.nightlabs.base.form.XFormToolkit.TOOLKIT_MODE;
import org.nightlabs.base.i18n.UnitRegistryEP;
import org.nightlabs.base.print.page.PredefinedPageEP;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.resolution.DPIResolutionUnit;
import org.nightlabs.editor2d.resolution.IResolutionUnit;
import org.nightlabs.editor2d.resolution.Resolution;
import org.nightlabs.editor2d.resolution.ResolutionImpl;
import org.nightlabs.editor2d.resolution.ResolutionUnitEP;
import org.nightlabs.editor2d.resolution.ResolutionUnitRegistry;
import org.nightlabs.editor2d.unit.UnitConstants;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.MMUnit;
import org.nightlabs.i18n.unit.UnitRegistry;
import org.nightlabs.print.page.A4Page;
import org.nightlabs.print.page.IPredefinedPage;
import org.nightlabs.print.page.PredefinedPageRegistry;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PageComposite 
extends XComposite 
{

	/**
	 * @param parent
	 * @param style
	 */
	public PageComposite(Composite parent, int style, TOOLKIT_MODE toolkitMode) 
	{
		super(parent, style);
		this.toolkitMode = toolkitMode;
		createComposite(this);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public PageComposite(Composite parent, int style, LayoutMode layoutMode,
			LayoutDataMode layoutDataMode, TOOLKIT_MODE toolkitMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.toolkitMode = toolkitMode;
		createComposite(this);		
	}

	private TOOLKIT_MODE toolkitMode;
	private XFormToolkit toolkit = null;
	private Combo pageSelectCombo = null;
	private Combo unitsCombo = null;
	private Combo resolutionUnitsCombo = null;
	private Text pageWidthText = null;
	private Text pageHeightText = null;
	private Text resolutionText = null;
		
	protected void createComposite(Composite parent) 
	{
		toolkit = new XFormToolkit(Display.getCurrent());
		toolkit.setCurrentMode(toolkitMode);
		
		Group comp = toolkit.createGroup(parent, SWT.NONE, 
				EditorPlugin.getResourceString("page.group.page.title"));
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Predefined Page Sizes
		Label pageSelectLabel = toolkit.createLabel(comp, 
				EditorPlugin.getResourceString("page.pageSelect.text"));
		pageSelectCombo = toolkit.createCombo(comp, SWT.BORDER);
		pageSelectCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Measurement Units
		Label unitsLabel = toolkit.createLabel(comp, 
				EditorPlugin.getResourceString("page.units.text"));
		unitsCombo = toolkit.createCombo(comp, SWT.BORDER);
		unitsCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		int textStyle = SWT.BORDER;
		
		// Width
		Label pageWidthLabel = toolkit.createLabel(comp, 
				EditorPlugin.getResourceString("page.pageWidth.text"));
		pageWidthText = toolkit.createText(comp, "", textStyle);
		pageWidthText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Height
		Label pageHeightLabel = toolkit.createLabel(comp, 
				EditorPlugin.getResourceString("page.pageHeight.text"));
		pageHeightText = toolkit.createText(comp, "", textStyle);
		pageHeightText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Resolution
		Label resolutionLabel = toolkit.createLabel(comp, 
				EditorPlugin.getResourceString("page.resolution.text"));
		Composite c = toolkit.createXComposite(comp, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// TODO: make it possible to determine resolutionX and resolutionY
		resolutionText = toolkit.createText(c, "", textStyle);
		resolutionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		resolutionUnitsCombo = toolkit.createCombo(c, SWT.BORDER);
				
		init();
	}
		
	protected void init() 
	{
		populatePageSelectCombo(PredefinedPageEP.sharedInstance().getPageRegistry());
		populateUnitsCombo(UnitRegistryEP.sharedInstance().getUnitRegistry());
		populateResolutionUnits(ResolutionUnitEP.sharedInstance().getResolutionUnitRegistry());
		
		resolutionText.setText(""+getResolution().getResolutionX());
		addListener();
	}
	
	protected void addListener() 
	{
		pageSelectCombo.addSelectionListener(pageSelectListener);
		unitsCombo.addSelectionListener(unitsListener);
		pageWidthText.addSelectionListener(widthListener);
		pageHeightText.addSelectionListener(heightListener);
		resolutionText.addSelectionListener(resolutionListener);
		resolutionUnitsCombo.addSelectionListener(resolutionUnitComboListener);
	}
	
	private SelectionListener pageSelectListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			IPredefinedPage page = pages.get(pageSelectCombo.getSelectionIndex());
			
			if (page.getUnit().getFactor() == unit.getFactor()) {
				pageHeightText.setText(""+page.getPageHeight());
				pageWidthText.setText(""+page.getPageWidth());				
			}
			else {
				setPageHeight(getPageHeight(), page.getUnit());
				setPageWidth(getPageWidth(), page.getUnit());
			}
		}	
	};
	
	private SelectionListener unitsListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			IUnit unit = units.get(unitsCombo.getSelectionIndex());			
			setPageWidth(getPageWidth(), unit);
			setPageHeight(getPageHeight(), unit);
			PageComposite.this.unit = unit;
		}	
	};

	private SelectionListener widthListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
//			pageWidth = Double.valueOf(pageWidthText.getText());			
		}	
	};

	private SelectionListener heightListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
//			pageHeight = Double.valueOf(pageHeightText.getText());
		}	
	};

	private SelectionListener resolutionListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			resolution.setResolutionX(Integer.valueOf(resolutionText.getText()));
			resolution.setResolutionY(Integer.valueOf(resolutionText.getText()));
		}	
	};
	
	private SelectionListener resolutionUnitListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			IResolutionUnit unit = resolutionUnits.get(resolutionUnitsCombo.getSelectionIndex());
			resolution.setResolutionUnit(unit);
			resolutionText.setText(""+resolution.getResolutionX());
		}	
	};	

	private SelectionListener resolutionUnitComboListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			IResolutionUnit unit = resolutionUnits.get(resolutionUnitsCombo.getSelectionIndex());
			resolution.setResolutionUnit(unit);
			resolutionText.setText(""+resolution.getResolutionX());
		}	
	};	
	
	
	private List<IPredefinedPage> pages = null;
	private void populatePageSelectCombo(PredefinedPageRegistry registry) 
	{
		pages = new LinkedList<IPredefinedPage>(registry.getPages());
		for (Iterator<IPredefinedPage> it = pages.iterator(); it.hasNext(); ) {
			IPredefinedPage page = it.next();
			pageSelectCombo.add(page.getName().getText(Locale.getDefault().getLanguage()));
		}
		if (registry.getPageIDs().contains(A4Page.PAGE_ID)) {
			int index = pages.indexOf(registry.getPage(A4Page.PAGE_ID));
			pageSelectCombo.select(index);
		} else if (pages.size() > 0) {
			pageSelectCombo.select(0);
		}
		
		if (pages.size() > 0) {
			IPredefinedPage page = pages.get(pageSelectCombo.getSelectionIndex());
			pageWidthText.setText(""+page.getPageWidth());
			pageHeightText.setText(""+page.getPageHeight());					
		}
	}
	
	private List<IUnit> units = null;
	private void populateUnitsCombo(UnitRegistry registry) 
	{
		units = new LinkedList<IUnit>(registry.getUnits(UnitConstants.UNIT_CONTEXT_EDITOR2D, true));
		for (Iterator<IUnit> it = units.iterator(); it.hasNext(); ) {
			IUnit unit = it.next();
			unitsCombo.add(unit.getName().getText(Locale.getDefault().getLanguage()));
		}
		unitsCombo.select(0);
//		if (registry.getUnitIDs().contains(MMUnit.UNIT_ID)) {
//			unitsCombo.select(units.indexOf(registry.getUnit(MMUnit.UNIT_ID)));			
//		} else if (units.size() > 0) {
//			unitsCombo.select(0);
//		}
	}
		
	private List<IResolutionUnit> resolutionUnits = null;
	private void populateResolutionUnits(ResolutionUnitRegistry registry)
	{
		resolutionUnits = new LinkedList<IResolutionUnit>(registry.getResolutionUnits());
		for (Iterator<IResolutionUnit> it = resolutionUnits.iterator(); it.hasNext(); ) {
			IResolutionUnit unit = it.next();
			resolutionUnitsCombo.add(unit.getName().getText(Locale.getDefault().getLanguage()));
		}
		if (registry.getResolutionIDs().contains(DPIResolutionUnit.RESOLUTION_ID)) {
			resolutionUnitsCombo.select(resolutionUnits.indexOf(registry.getResolutionUnit(DPIResolutionUnit.RESOLUTION_ID)));			
		} else if (resolutionUnits.size() > 0) {
			resolutionUnitsCombo.select(0);
		}
	}
	
	private Resolution resolution = new ResolutionImpl();
	public Resolution getResolution() {
		return resolution;
	}
	
	private double pageWidth = 210;
	public double getPageWidth() 
	{
		if (!pageWidthText.isDisposed()) {
			try {
				pageWidth = Double.valueOf(pageWidthText.getText());
			} catch (NumberFormatException nfe) {
			
			}
		}			 
		return pageWidth;
	}
	private void setPageWidth(double value, IUnit unit) 
	{
		double pageWidth = getPageWidth();
		if (this.unit.getFactor() == unit.getFactor())		
			pageWidth = value;
		else {
			double oldFactor = this.unit.getFactor();
			pageWidth = (pageWidth * unit.getFactor()) / oldFactor;			
		}
		pageWidthText.setText(""+pageWidth);
	}
	
	private double pageHeight = 297;
	public double getPageHeight() 
	{
		if (!pageHeightText.isDisposed()) 
		{
			try {
				pageHeight = Double.valueOf(pageHeightText.getText());
			} catch (NumberFormatException nfe) {
			}			
		}
		return pageHeight;
	}
	private void setPageHeight(double value, IUnit unit) 
	{
		double pageHeight = getPageHeight();
		if (this.unit.getFactor() == unit.getFactor())			
			pageHeight = value;
		else {			
			double oldFactor = this.unit.getFactor();
			pageHeight = (pageHeight * unit.getFactor()) / oldFactor;						
		}
		pageHeightText.setText(""+pageHeight);
	}
	
	private IUnit unit = new MMUnit();
	public IUnit getUnit() {
		return unit;
	}
	
//	public PageSize getPageSize() 
//	{
//		return new PageSize(getPageWidth(), getPageHeight(), getUnit(), getResolution());
//	}
}
