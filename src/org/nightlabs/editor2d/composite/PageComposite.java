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
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.page.IPredefinedPage;
import org.nightlabs.editor2d.page.IUnit;
import org.nightlabs.editor2d.page.PageRegistry;
import org.nightlabs.editor2d.page.PageRegistryEP;
import org.nightlabs.editor2d.page.resolution.IResolutionUnit;
import org.nightlabs.editor2d.page.resolution.Resolution;
import org.nightlabs.editor2d.page.resolution.ResolutionImpl;

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

	protected TOOLKIT_MODE toolkitMode;
	protected XFormToolkit toolkit = null;
	protected Combo pageSelectCombo = null;
	protected Combo unitsCombo = null;
	protected Combo resolutionUnitsCombo = null;
	protected Text pageWidthText = null;
	protected Text pageHeightText = null;
	protected Text resolutionText = null;
		
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
		
		resolutionText = toolkit.createText(c, "", textStyle);
		resolutionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		resolutionUnitsCombo = toolkit.createCombo(c, SWT.BORDER);
				
		init();
	}
		
	protected void init() 
	{		
		PageRegistry registry = PageRegistryEP.sharedInstance().getPageRegistry();
		populatePageSelectCombo(registry);
		populateUnitsCombo(registry);
		populateResolutionUnits(registry);
		
		resolutionText.setText(""+getResolution().getResolution());
		addListener();
	}
	
	protected void addListener() 
	{
		pageSelectCombo.addSelectionListener(pageSelectListener);
		unitsCombo.addSelectionListener(unitsListener);
		pageWidthText.addSelectionListener(widthListener);
		pageHeightText.addSelectionListener(heightListener);
		resolutionText.addSelectionListener(resolutionListener);
	}
	
	protected SelectionListener pageSelectListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			IPredefinedPage page = pages.get(pageSelectCombo.getSelectionIndex());
			pageHeightText.setText(""+page.getPageHeight());
			pageWidthText.setText(""+page.getPageWidth());
		}	
	};
	
	protected SelectionListener unitsListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			unit = units.get(unitsCombo.getSelectionIndex());			
		}	
	};

	protected SelectionListener widthListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			pageWidth = Double.valueOf(pageWidthText.getText());			
		}	
	};

	protected SelectionListener heightListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			pageHeight = Double.valueOf(pageHeightText.getText());
		}	
	};

	protected SelectionListener resolutionListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			resolution.setResolution(Integer.valueOf(resolutionText.getText()));
		}	
	};
	
	protected SelectionListener resolutionUnitListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			IResolutionUnit unit = resolutionUnits.get(resolutionUnitsCombo.getSelectionIndex());
			resolution.setResolutionUnit(unit);
			resolutionText.setText(""+resolution.getResolution());
		}	
	};	
	
	private List<IPredefinedPage> pages = null;
	protected void populatePageSelectCombo(PageRegistry registry) 
	{
		pages = new LinkedList<IPredefinedPage>(registry.getPredefinedPages());
		for (Iterator<IPredefinedPage> it = pages.iterator(); it.hasNext(); ) {
			IPredefinedPage page = it.next();
			pageSelectCombo.add(page.getName().getText(Locale.getDefault().getLanguage()));
		}
		if (pages.size() > 0) {
			pageSelectCombo.setText("DIN A4");
		}
	}
	
	private List<IUnit> units = null;
	protected void populateUnitsCombo(PageRegistry registry) 
	{
		units = new LinkedList<IUnit>(registry.getUnits());
		for (Iterator<IUnit> it = units.iterator(); it.hasNext(); ) {
			IUnit unit = it.next();
			unitsCombo.add(unit.getName().getText(Locale.getDefault().getLanguage()));
		}
		if (units.size() > 0) {
			unitsCombo.setText("mm");			
		}
	}
		
	private List<IResolutionUnit> resolutionUnits = null;
	protected void populateResolutionUnits(PageRegistry registry)
	{
		resolutionUnits = new LinkedList<IResolutionUnit>(registry.getResolutionUnits());
		for (Iterator<IResolutionUnit> it = resolutionUnits.iterator(); it.hasNext(); ) {
			IResolutionUnit unit = it.next();
			resolutionUnitsCombo.add(unit.getName().getText(Locale.getDefault().getLanguage()));
		}
		if (resolutionUnits.size() > 0) {
			resolutionUnitsCombo.setText("DPI");
		}
	}
	
	protected Resolution resolution = new ResolutionImpl();
	public Resolution getResolution() {
		return resolution;
	}
	
	protected double pageWidth = 0;
	public double getPageWidth() {
		return pageWidth;
	}
	
	protected double pageHeight = 0;
	public double getPageHeight() {
		return pageHeight;
	}
	
	protected IUnit unit = null;
	public IUnit getUnit() {
		return unit;
	}
}
