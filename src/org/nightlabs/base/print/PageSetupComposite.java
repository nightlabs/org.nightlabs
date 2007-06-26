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
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.CComboComposite;
import org.nightlabs.base.composite.DoubleSpinnerComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.i18n.UnitRegistryEP;
import org.nightlabs.base.labelprovider.UnitLabelProvider;
import org.nightlabs.base.print.page.PredefinedPageEP;
import org.nightlabs.i18n.unit.DefaultScreenUnit;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.MMUnit;
import org.nightlabs.i18n.unit.UnitRegistry;
import org.nightlabs.i18n.unit.UnitUtil;
import org.nightlabs.print.page.A4Page;
import org.nightlabs.print.page.IPredefinedPage;

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
	
	private Button orientationHorizontal = null;
	private Button orientationVertical = null;	
	private CComboComposite<IPredefinedPage> predefinedPageCombo = null;
	private PrintPreviewComposite previewComp = null;
	
	protected void init(PageFormat pageFormat) 
	{
		if (pageFormat == null) 
			throw new IllegalArgumentException("Param pageFormat must not be null!");
		
		this.pageFormat = pageFormat;
		initMargins(pageFormat);
		createComposite(this);
		initOrientation(pageFormat);
		setSpinnerValues();
		addDisposeListener(disposeListener);
	}
	
	protected PrintPreviewComposite initPreviewComposite(Composite parent) 
	{
		return new PrintPreviewComposite(pageFormat, parent, SWT.NONE);
	}
	
	private void createComposite(Composite parent) 
	{
		setLayout(new GridLayout(2, false));
		
		// preview
		Group previewGroup = new Group(parent, SWT.NONE);
		previewGroup.setLayout(new GridLayout());
		previewGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		previewGroup.setText(NLBasePlugin.getResourceString("composite.pageSetup.group.preview.label"));
		previewComp = initPreviewComposite(previewGroup);
		
		Composite detailComp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridData detailData = new GridData(125, SWT.DEFAULT);
		detailComp.setLayoutData(detailData);
		
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

		createMargins(detailComp);
	}	
	
	private double getSpinnerMaximum() {
		return Integer.MAX_VALUE;
	}
	
	private DoubleSpinnerComposite marginTopSpinner = null;
	private DoubleSpinnerComposite marginBottomSpinner = null;	
	private DoubleSpinnerComposite marginLeftSpinner = null;	
	private DoubleSpinnerComposite marginRightSpinner = null;		
	private void createMargins(Composite parent) 
	{
		Group marginGroup = new Group(parent, SWT.NONE);
		marginGroup.setText(NLBasePlugin.getResourceString("composite.pageSetup.group.margins.label"));
		marginGroup.setLayout(new GridLayout(2, false));				
		marginGroup.setLayoutData(new GridData(GridData.FILL_BOTH));						
		
		Label unitLabel = new Label(marginGroup, SWT.NONE);
		unitLabel.setText(NLBasePlugin.getResourceString("composite.printPreview.unit.label"));
		unitCombo = initUnitCombo(marginGroup);
		unitCombo.addSelectionListener(unitListener);
		unitCombo.selectElement(new MMUnit());
	
		Label marginTopLabel = new Label(marginGroup, SWT.NONE);
		marginTopLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginTop.label"));		
		marginTopSpinner = new DoubleSpinnerComposite(marginGroup, SWT.NONE, SWT.BORDER, 
				getSpinnerDigits(), 0, getSpinnerMaximum(), getSpinnerIncrement(), 
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		marginTopSpinner.setValue(marginTop);
		marginTopSpinner.addSelectionListener(marginListener);		
		marginTopSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		Label marginLeftLabel = new Label(marginGroup, SWT.NONE);
		marginLeftLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginLeft.label"));		
		marginLeftSpinner = new DoubleSpinnerComposite(marginGroup, SWT.NONE, SWT.BORDER, 
				getSpinnerDigits(), 0, getSpinnerMaximum(), getSpinnerIncrement(), 
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		marginLeftSpinner.setValue(marginLeft);
		marginLeftSpinner.addSelectionListener(marginListener);
		marginLeftSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label marginRightLabel = new Label(marginGroup, SWT.NONE);			
		marginRightLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginRight.label"));		
		marginRightSpinner = new DoubleSpinnerComposite(marginGroup, SWT.NONE, SWT.BORDER, 
				getSpinnerDigits(), 0, getSpinnerMaximum(), getSpinnerIncrement(), 
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		marginRightSpinner.setValue(marginRight);
		marginRightSpinner.addSelectionListener(marginListener);
		marginRightSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		

		Label marginBottomLabel = new Label(marginGroup, SWT.NONE);
		marginBottomLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginBottom.label"));		
		marginBottomSpinner = new DoubleSpinnerComposite(marginGroup, SWT.NONE, SWT.BORDER, 
				getSpinnerDigits(), 0, getSpinnerMaximum(), getSpinnerIncrement(), 
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		marginBottomSpinner.setValue(marginBottom);
		marginBottomSpinner.addSelectionListener(marginListener);		
		marginBottomSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));				
	}
	
//	private Spinner marginTopSpinner = null;
//	private Spinner marginBottomSpinner = null;	
//	private Spinner marginLeftSpinner = null;	
//	private Spinner marginRightSpinner = null;		
//	private void createMargins(Composite parent) 
//	{
//		// margins
//		Group marginGroup = new Group(parent, SWT.NONE);
//		marginGroup.setText(NLBasePlugin.getResourceString("composite.pageSetup.group.margins.label"));
//		marginGroup.setLayout(new GridLayout(2, false));		
////	marginGroup.setLayout(new GridLayout());		
//		marginGroup.setLayoutData(new GridData(GridData.FILL_BOTH));						
//		
//		Label unitLabel = new Label(marginGroup, SWT.NONE);
//		unitLabel.setText(NLBasePlugin.getResourceString("composite.printPreview.unit.label"));
//		unitCombo = initUnitCombo(marginGroup);
//		unitCombo.addSelectionListener(unitListener);
//		unitCombo.selectElement(new MMUnit());
//	
//		Label marginTopLabel = new Label(marginGroup, SWT.NONE);
//		marginTopLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginTop.label"));		
//		marginTopSpinner = new Spinner(marginGroup, SWT.BORDER);
//		configureMarginSpinner(marginTopSpinner);
//		marginTopSpinner.setSelection((int)previewComp.getMarginTop());
//		marginTopSpinner.addSelectionListener(marginListener);
////		marginTopSpinner.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));		
//		marginTopSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
//		
////		Composite middleSpinner = new XComposite(marginGroup, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
////		middleSpinner.setLayout(new GridLayout(2, true));
//
//		Label marginLeftLabel = new Label(marginGroup, SWT.NONE);
//		marginLeftLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginLeft.label"));		
//		marginLeftSpinner = new Spinner(marginGroup, SWT.BORDER);
//		configureMarginSpinner(marginLeftSpinner);
//		marginLeftSpinner.setSelection((int)previewComp.getMarginLeft());
//		marginLeftSpinner.addSelectionListener(marginListener);
////		marginLeftSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
//		marginLeftSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		Label marginRightLabel = new Label(marginGroup, SWT.NONE);			
//		marginRightLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginRight.label"));		
//		marginRightSpinner = new Spinner(marginGroup, SWT.BORDER);
//		configureMarginSpinner(marginRightSpinner);
//		marginRightSpinner.setSelection((int)previewComp.getMarginRight());
//		marginRightSpinner.addSelectionListener(marginListener);
////		marginRightSpinner.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
//		marginRightSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
//
//		Label marginBottomLabel = new Label(marginGroup, SWT.NONE);
//		marginBottomLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginBottom.label"));		
//		marginBottomSpinner = new Spinner(marginGroup, SWT.BORDER);
//		configureMarginSpinner(marginBottomSpinner);
//		marginBottomSpinner.setSelection((int)previewComp.getMarginBottom());
//		marginBottomSpinner.addSelectionListener(marginListener);
////		marginBottomSpinner.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));		
//		marginBottomSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));				
//	}

	// change 1/72 inch value into currentUnit and display it in spinner	
	private void setSpinnerValue(DoubleSpinnerComposite s, double value) 
	{
		double spinnerValue = UnitUtil.getUnitValue(value, getScreenUnit(), getCurrentUnit());
		s.setValue(spinnerValue);
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("spinnerValue = "+spinnerValue);
//			logger.debug("pageFormatValue = "+value);			
//		}		
	}

	// change currentUnit value into 1/72 dpi	
	private double getSpinnerValue(DoubleSpinnerComposite s)  
	{
		double spinnerValue = s.getValue();
		double pageFormatValue = UnitUtil.getUnitValue(spinnerValue, getCurrentUnit(), getScreenUnit());
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("spinnerValue = "+spinnerValue);
//			logger.debug("pageFormatValue = "+pageFormatValue);			
//		}
		
		return pageFormatValue;
	}
	
//	private void assignMarginValues() 
//	{
//		marginTop = marginTopSpinner.getValue();
//		marginBottom = marginBottomSpinner.getValue();
//		marginLeft = marginLeftSpinner.getValue();
//		marginRight = marginRightSpinner.getValue();				
//	}
	
	private void assignMarginValues() 
	{
		marginTop = getSpinnerValue(marginTopSpinner);
		marginBottom = getSpinnerValue(marginBottomSpinner);
		marginLeft = getSpinnerValue(marginLeftSpinner);
		marginRight = getSpinnerValue(marginRightSpinner);				
	}	
	
	private void setSpinnerValues() 
	{
		setSpinnerValue(marginTopSpinner, marginTop);
		setSpinnerValue(marginBottomSpinner, marginBottom);		
		setSpinnerValue(marginLeftSpinner, marginLeft);		
		setSpinnerValue(marginRightSpinner, marginRight);		
	}
	
	private SelectionListener marginListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) 
		{			
			assignMarginValues();	
			changePageFormat();
			refresh(pageFormat);
			logger.debug("margin changed");
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);			
		}	
	};	
	
	
	private IUnit getScreenUnit() {
		return UnitRegistry.sharedInstance().getUnit(DefaultScreenUnit.UNIT_ID);
	}
	
	public IUnit getCurrentUnit() {
		return unitCombo.getSelectedElement();
	}
	private CComboComposite<IUnit> unitCombo = null;
	private CComboComposite<IUnit> initUnitCombo(Composite parent) {
		List<IUnit> units = new ArrayList<IUnit>(UnitRegistryEP.sharedInstance().getUnitRegistry().getGlobalUnits());
		return new CComboComposite<IUnit>(units, new UnitLabelProvider(), parent, SWT.NONE, null, getBorderStyle() | SWT.READ_ONLY);
	}
		
	private SelectionListener unitListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) {
			setSpinnerValues();
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}	
	};
	
	private int spinnerDigits = 3;
	private int getSpinnerDigits() {
		return spinnerDigits;
	}
	
	private double spinnerIncrement = 100.0;
	private double getSpinnerIncrement() {
		return spinnerIncrement;
	}
	
	private void initOrientation(PageFormat pf) 
	{
		if (pf.getOrientation() == PageFormat.LANDSCAPE)
			orientationHorizontal.setSelection(true);
		if (pf.getOrientation() == PageFormat.PORTRAIT)
			orientationVertical.setSelection(true);		
	}
	
	private double marginTop;
	private double marginBottom;	
	private double marginLeft;	
	private double marginRight;		
	private void initMargins(PageFormat pf) 
	{
		marginTop = pf.getImageableY();
		marginBottom = ((pf.getHeight() - (pf.getImageableY() + pf.getImageableHeight())));
		marginLeft = pf.getImageableX();
		marginRight = (pf.getWidth() - (pf.getImageableX() + pf.getImageableWidth()));														
	}
					
	private void changePageFormat() 
	{
		Paper paper = pageFormat.getPaper();		
		if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
			paper.setImageableArea(marginLeft, marginTop, (paper.getWidth() - (marginLeft + marginRight)), 
					(paper.getHeight() - (marginTop + marginBottom)) );					
		} 
		else if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
			paper.setImageableArea(marginTop, marginRight, (paper.getWidth() - (marginBottom + marginTop)), 
					(paper.getHeight() - (marginLeft + marginRight)) );						
		}
		pageFormat.setPaper(paper);		
	}
			
	private CComboComposite<IPredefinedPage> initPageCombo(Composite parent) 
	{
		List<IPredefinedPage> pages = new ArrayList<IPredefinedPage>(PredefinedPageEP.sharedInstance().getPageRegistry().getPages());
		return new CComboComposite<IPredefinedPage>(pages, predefinedPageLabelProvider, 
				parent, SWT.READ_ONLY, null);
	}
		
	private PageFormat getPageFormat(IPredefinedPage page) 
	{		
		IUnit pageUnit = page.getUnit();
//		IUnit inchUnit = UnitRegistry.sharedInstance().getUnit(InchUnit.UNIT_ID);		
//		double heightInInch = UnitUtil.getUnitValue(page.getPageHeight(), pageUnit, inchUnit);
//		double widthInInch = UnitUtil.getUnitValue(page.getPageWidth(), pageUnit, inchUnit);
//		double factor = 72f;
//		double height = heightInInch * factor;
//		double width = widthInInch * factor;
		
		double height = UnitUtil.getUnitValue(page.getPageHeight(), pageUnit, getScreenUnit());
		double width = UnitUtil.getUnitValue(page.getPageWidth(), pageUnit, getScreenUnit());		
				
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
			refresh(pageFormat);
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);			
		}	
	};
		
	private SelectionListener horizontalListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) {
			pageFormat.setOrientation(PageFormat.LANDSCAPE);
			refresh(pageFormat);
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};

	private SelectionListener verticalListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) {
			pageFormat.setOrientation(PageFormat.PORTRAIT);
			refresh(pageFormat);
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};
		
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
