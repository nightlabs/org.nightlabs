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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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
import org.nightlabs.print.page.A3Page;
import org.nightlabs.print.page.A4Page;
import org.nightlabs.print.page.A5Page;
import org.nightlabs.print.page.IPredefinedPage;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PageSetupComposite 
extends XComposite 
{
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
		marginGroup.setLayout(new GridLayout(2, false));		
		marginGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		
		marginTopSpinner = new Spinner(marginGroup, SWT.BORDER);
		configureMarginSpinner(marginTopSpinner);
		marginTopSpinner.setSelection((int)previewComp.getMarginTop());
		marginTopSpinner.addSelectionListener(marginListener);
		Label marginTopLabel = new Label(marginGroup, SWT.NONE);
		marginTopLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginTop.label"));
		
		marginLeftSpinner = new Spinner(marginGroup, SWT.BORDER);
		configureMarginSpinner(marginLeftSpinner);
		marginLeftSpinner.setSelection((int)previewComp.getMarginLeft());
		marginLeftSpinner.addSelectionListener(marginListener);
		Label marginLeftLabel = new Label(marginGroup, SWT.NONE);
		marginLeftLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginLeft.label"));

		marginRightSpinner = new Spinner(marginGroup, SWT.BORDER);
		configureMarginSpinner(marginRightSpinner);
		marginRightSpinner.setSelection((int)previewComp.getMarginRight());
		marginRightSpinner.addSelectionListener(marginListener);
		Label marginRightLabel = new Label(marginGroup, SWT.NONE);
		marginRightLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginRight.label"));

		marginBottomSpinner = new Spinner(marginGroup, SWT.BORDER);
		configureMarginSpinner(marginBottomSpinner);
		marginBottomSpinner.setSelection((int)previewComp.getMarginBottom());
		marginBottomSpinner.addSelectionListener(marginListener);
		Label marginBottomLabel = new Label(marginGroup, SWT.NONE);
		marginBottomLabel.setText(NLBasePlugin.getResourceString("composite.pageSetup.marginBottom.label"));
		
		setValues(pageFormat);
	}
		
	private void configureMarginSpinner(Spinner s) 
	{
		s.setMinimum(0);
		s.setMaximum(200);		
	}

	public void setValues(PageFormat pf) 
	{
		if (pf.getOrientation() == PageFormat.LANDSCAPE)
			orientationHorizontal.setSelection(true);
		if (pf.getOrientation() == PageFormat.PORTRAIT)
			orientationVertical.setSelection(true);		
		
		// TODO: marginSpinner Values should be set here		
	}
	
	private CComboComposite<IPredefinedPage> initPageCombo(Composite parent) 
	{
		List<IPredefinedPage> pages = new LinkedList<IPredefinedPage>();
		pages.add(new A4Page());
		pages.add(new A5Page());		
		pages.add(new A3Page());	
		return new CComboComposite<IPredefinedPage>(pages, predefinedPageLabelProvider, 
				parent, SWT.READ_ONLY);
	}
	
	private PageFormat getPageFormat(IPredefinedPage page) 
	{
		// TODO implement this method
		return pageFormat;
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
//		redraw();
//		update();			
	}
	
}
