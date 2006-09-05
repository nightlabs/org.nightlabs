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
package org.nightlabs.base.print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.CComboComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.i18n.UnitRegistryEP;
import org.nightlabs.base.labelprovider.UnitLabelProvider;
import org.nightlabs.base.print.page.PredefinedPageEP;
import org.nightlabs.i18n.unit.DefaultScreenUnit;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.InchUnit;
import org.nightlabs.i18n.unit.MMUnit;
import org.nightlabs.i18n.unit.UnitRegistry;
import org.nightlabs.i18n.unit.UnitUtil;
import org.nightlabs.print.PrintUtil;
import org.nightlabs.print.page.A3Page;
import org.nightlabs.print.page.A4Page;
import org.nightlabs.print.page.A5Page;
import org.nightlabs.print.page.IPredefinedPage;
import org.nightlabs.util.Utils;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PageSetupComposite 
extends XComposite 
{
	public static final Logger logger = Logger.getLogger(PageSetupComposite.class);
	
	public PageSetupComposite(PageFormat pageFormat, Composite parent, int style) {
		super(parent, style);
		init(pageFormat);
	}

	public PageSetupComposite(PageFormat pageFormat, Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		init(pageFormat);
	}
	
	private PageFormat pageFormat = null;
	public PageFormat getPageFormat() {
		return pageFormat;
	}
	private Spinner marginTopSpinner = null;
	private Spinner marginBottomSpinner = null;	
	private Spinner marginLeftSpinner = null;	
	private Spinner marginRightSpinner = null;	
	private Button orientationHorizontal = null;
	private Button orientationVertical = null;	
	private CComboComposite<IPredefinedPage> predefinedPageCombo = null;
	private PrintPreviewComposite previewComp = null;
	
	protected void init(PageFormat pageFormat) 
	{
		if (pageFormat == null) 
			throw new IllegalArgumentException("Param pageFormat must not be null!");
		
		this.pageFormat = pageFormat;
		createComposite(this);
		addDisposeListener(disposeListener);
	}
	
	protected PrintPreviewComposite initPreviewComposite(Composite parent) 
	{
		return new PrintPreviewComposite(pageFormat, parent, SWT.NONE);
	}
	
	private void createComposite(Composite parent) 
	{
		setLayout(new GridLayout(2, true));
		
		// preview
		Group previewGroup = new Group(parent, SWT.NONE);
		previewGroup.setLayout(new GridLayout());
		previewGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		previewGroup.setText(NLBasePlugin.getResourceString("composite.pageSetup.group.preview.label"));
		previewComp = initPreviewComposite(previewGroup);
		
		Composite detailComp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		
		// predefined pages		
		Group pageGroup = new Group(detailComp, SWT.NONE);
		pageGroup.setText(NLBasePlugin.getResourceString("composite.pageSetup.predefinedPage.label"));
		pageGroup.setLayout(new GridLayout());
		pageGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		predefinedPageCombo = initPageCombo(pageGroup);
		predefinedPageCombo.addSelectionListener(pageListener);
		predefinedPageCombo.selectElement(new A4Page());
		
		// orientation		
		Group orientationGroup = new Group(detailComp, SWT.NONE);
		orientationGroup.setText(NLBasePlugin.getResourceString("composite.pageSetup.orientation.label"));
		orientationGroup.setLayout(new GridLayout());
		orientationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		orientationHorizontal = new Button(orientationGroup, SWT.RADIO);
		orientationHorizontal.setText(NLBasePlugin.getResourceString("composite.pageSetup.horizontal.label"));
		orientationHorizontal.addSelectionListener(horizontalListener);		
		orientationVertical = new Button(orientationGroup, SWT.RADIO);
		orientationVertical.setText(NLBasePlugin.getResourceString("composite.pageSetup.vertical.label"));
		orientationVertical.addSelectionListener(verticalListener);

		// margins
		Group marginGroup = new Group(detailComp, SWT.NONE);
		marginGroup.setText(NLBasePlugin.getResourceString("composite.pageSetup.group.margins.label"));
//		marginGroup.setLayout(new GridLayout(2, false));		
//		marginGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		marginGroup.setLayout(new GridLayout());		
		marginGroup.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
//		Label unitLabel = new Label(marginGroup, SWT.NONE);
//		unitLabel.setText(NLBasePlugin.getResourceString("composite.printPreview.unit.label"));
//		unitCombo = initUnitCombo(marginGroup);
//		unitCombo.addSelectionListener(unitListener);
//		unitCombo.selectElement(new MMUnit());
		
		marginTopSpinner = new Spinner(marginGroup, SWT.BORDER);
		configureMarginSpinner(marginTopSpinner);
		marginTopSpinner.setSelection((int)previewComp.getMarginTop());
		marginTopSpinner.addSelectionListener(marginListener);
//		Label marginTopLabel = new Label(marginGroup, SWT.NONE);
//		marginTopLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginTop.label"));
		marginTopSpinner.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));		
		
		Composite middleSpinner = new XComposite(marginGroup, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		middleSpinner.setLayout(new GridLayout(2, true));
			
		marginLeftSpinner = new Spinner(middleSpinner, SWT.BORDER);
		configureMarginSpinner(marginLeftSpinner);
		marginLeftSpinner.setSelection((int)previewComp.getMarginLeft());
		marginLeftSpinner.addSelectionListener(marginListener);
//		Label marginLeftLabel = new Label(marginGroup, SWT.NONE);
//		marginLeftLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginLeft.label"));
		marginLeftSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		
		marginRightSpinner = new Spinner(middleSpinner, SWT.BORDER);
		configureMarginSpinner(marginRightSpinner);
		marginRightSpinner.setSelection((int)previewComp.getMarginRight());
		marginRightSpinner.addSelectionListener(marginListener);
//		Label marginRightLabel = new Label(marginGroup, SWT.NONE);
//		marginRightLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginRight.label"));
		marginRightSpinner.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		
		marginBottomSpinner = new Spinner(marginGroup, SWT.BORDER);
		configureMarginSpinner(marginBottomSpinner);
		marginBottomSpinner.setSelection((int)previewComp.getMarginBottom());
		marginBottomSpinner.addSelectionListener(marginListener);
//		Label marginBottomLabel = new Label(marginGroup, SWT.NONE);
//		marginBottomLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginBottom.label"));
		marginBottomSpinner.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));		
		
		setValues(pageFormat);
	}	
	
	private IUnit getScreenUnit() {
		return UnitRegistry.sharedInstance().getUnit(DefaultScreenUnit.UNIT_ID);
	}
	
	private IUnit currentUnit = null;
	public IUnit getCurrentUnit() {
		return currentUnit;
	}
	private CComboComposite<IUnit> unitCombo = null;
	private CComboComposite<IUnit> initUnitCombo(Composite parent) {
		List<IUnit> units = new ArrayList<IUnit>(UnitRegistryEP.sharedInstance().getUnitRegistry().getGlobalUnits());
		return new CComboComposite<IUnit>(units, new UnitLabelProvider(), parent, SWT.NONE, SWT.BORDER | SWT.READ_ONLY);
	}
		
	private SelectionListener unitListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) {
			currentUnit = unitCombo.getSelectedElement();
			setMarginValues(pageFormat);
			
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}	
	};
	
	private int spinnerDigits = 2;
	private void configureMarginSpinner(Spinner s) 
	{
		s.setMinimum(0);
		s.setMaximum(200);
//		s.setDigits(spinnerDigits);
	}

	public void setValues(PageFormat pf) 
	{
		if (pf.getOrientation() == PageFormat.LANDSCAPE)
			orientationHorizontal.setSelection(true);
		if (pf.getOrientation() == PageFormat.PORTRAIT)
			orientationVertical.setSelection(true);		
	
//		setMarginValues(pf);
	}
	
	protected void setMarginValues(PageFormat pf) 
	{
		double marginTop = pf.getImageableY();
		double marginBottom = ((pf.getHeight() - (pf.getImageableY() + pf.getImageableHeight())));
		double marginLeft = pf.getImageableX();
		double marginRight = (pf.getWidth() - (pf.getImageableX() + pf.getImageableWidth()));												
		
		setMarginValue(marginTopSpinner, marginTop, getScreenUnit());
		setMarginValue(marginBottomSpinner, marginBottom, getScreenUnit());		
		setMarginValue(marginLeftSpinner, marginLeft, getScreenUnit());
		setMarginValue(marginRightSpinner, marginRight, getScreenUnit());						
	}
	
	protected void setMarginValue(Spinner s, double value, IUnit unit) 
	{
		double val = UnitUtil.getUnitValue(value, unit, getScreenUnit());
		s.setSelection((int)val);
		if (s.equals(marginTopSpinner))
			previewComp.setMarginTop(val);							
		if (s.equals(marginBottomSpinner))		
			previewComp.setMarginBottom(val);								
		if (s.equals(marginLeftSpinner))				
			previewComp.setMarginLeft(val);												
		if (s.equals(marginRightSpinner))				
			previewComp.setMarginRight(val);																			
	}
	
	private CComboComposite<IPredefinedPage> initPageCombo(Composite parent) 
	{
		List<IPredefinedPage> pages = new ArrayList<IPredefinedPage>(PredefinedPageEP.sharedInstance().getPageRegistry().getPages());
		return new CComboComposite<IPredefinedPage>(pages, predefinedPageLabelProvider, 
				parent, SWT.READ_ONLY);
	}
		
//	private PageFormat getPageFormat(IPredefinedPage page) 
//	{		
//		IUnit inchUnit = UnitRegistry.sharedInstance().getUnit(InchUnit.UNIT_ID);
//		IUnit pageUnit = page.getUnit();
//		double heightInInch = UnitUtil.getUnitValue(page.getPageHeight(), pageUnit, inchUnit);
//		double widthInInch = UnitUtil.getUnitValue(page.getPageWidth(), pageUnit, inchUnit);
//		double factor = 72f;
//		double height = heightInInch * factor;
//		double width = widthInInch * factor;
//		
//		double marginTop = previewComp.getMarginTop();
//		double marginLeft = previewComp.getMarginLeft();
//		double marginRight = previewComp.getMarginRight();
//		double marginBottom = previewComp.getMarginBottom();
//		
//		Paper paper = pageFormat.getPaper();
//		paper.setImageableArea(marginTop, marginLeft, width, height);
//		paper.setSize(marginLeft + width + marginRight, marginTop + height + marginBottom);
//		PageFormat newPageFormat = (PageFormat) pageFormat.clone();
//		newPageFormat.setPaper(paper);
//		
//		return newPageFormat;
//	}
		
	private PageFormat getPageFormat(IPredefinedPage page) 
	{		
		IUnit screenUnit = getScreenUnit();
		IUnit pageUnit = page.getUnit();
		double height = UnitUtil.getUnitValue(page.getPageHeight(), pageUnit, screenUnit);
		double width = UnitUtil.getUnitValue(page.getPageWidth(), pageUnit, screenUnit);
		
		double marginTop = previewComp.getMarginTop();
		double marginLeft = previewComp.getMarginLeft();
		double marginRight = previewComp.getMarginRight();
		double marginBottom = previewComp.getMarginBottom();
		
		Paper paper = pageFormat.getPaper();
		paper.setImageableArea(marginTop, marginLeft, width, height);
		paper.setSize(marginLeft + width + marginRight, marginTop + height + marginBottom);
		PageFormat newPageFormat = (PageFormat) pageFormat.clone();
		newPageFormat.setPaper(paper);
		
		return newPageFormat;
	}	
	
	private LabelProvider predefinedPageLabelProvider = new LabelProvider() 
	{
		@Override
		public String getText(Object element) 
		{			
			IPredefinedPage page = (IPredefinedPage) element; 
			return page.getName().getText(Locale.getDefault().getLanguage());
		}		
	};
	
	private SelectionListener pageListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) {
			pageFormat = getPageFormat(predefinedPageCombo.getSelectedElement());
			refresh();
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);			
		}	
	};
		
	private SelectionListener horizontalListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) {
			pageFormat.setOrientation(PageFormat.LANDSCAPE);
			refresh();
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};

	private SelectionListener verticalListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) {
			pageFormat.setOrientation(PageFormat.PORTRAIT);
			refresh();
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};
	
	private SelectionListener marginListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) 
		{
			if (e.getSource().equals(marginTopSpinner))
				previewComp.setMarginTop(marginTopSpinner.getSelection());
			if (e.getSource().equals(marginBottomSpinner))
				previewComp.setMarginBottom(marginBottomSpinner.getSelection());
			if (e.getSource().equals(marginLeftSpinner))
				previewComp.setMarginLeft(marginLeftSpinner.getSelection());
			if (e.getSource().equals(marginRightSpinner))
				previewComp.setMarginRight(marginRightSpinner.getSelection());			
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);			
		}	
	};

//	protected double getSpinnerValue(Spinner s) {
//		return Utils.getDouble(s.getSelection(), spinnerDigits);
//	}
//	
//	private SelectionListener marginListener = new SelectionListener()
//	{	
//		public void widgetSelected(SelectionEvent e) 
//		{
//			if (e.getSource().equals(marginTopSpinner))
//				setMarginValue(marginTopSpinner, getSpinnerValue(marginTopSpinner), getCurrentUnit());			
//			if (e.getSource().equals(marginBottomSpinner))
//				setMarginValue(marginTopSpinner, getSpinnerValue(marginBottomSpinner), getCurrentUnit());
//			if (e.getSource().equals(marginLeftSpinner))
//				setMarginValue(marginLeftSpinner, getSpinnerValue(marginLeftSpinner), getCurrentUnit());			
//			if (e.getSource().equals(marginRightSpinner))
//				setMarginValue(marginRightSpinner, getSpinnerValue(marginRightSpinner), getCurrentUnit());
//		}	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);			
//		}	
//	};
	
	private DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) {
			orientationHorizontal.removeSelectionListener(horizontalListener);
			orientationVertical.removeSelectionListener(verticalListener);
			marginBottomSpinner.removeSelectionListener(marginListener);
			marginTopSpinner.removeSelectionListener(marginListener);
			marginLeftSpinner.removeSelectionListener(marginListener);
			marginRightSpinner.removeSelectionListener(marginListener);
		}	
	};
	
	protected void refresh() 
	{
		previewComp.setPageFormat(pageFormat);		
	}
	
	public void refresh(PageFormat pageFormat) {
		this.pageFormat = pageFormat;
		refresh();
	}
	
}
