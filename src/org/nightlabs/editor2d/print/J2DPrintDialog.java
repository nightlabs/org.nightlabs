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
package org.nightlabs.editor2d.print;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.dialog.CenteredDialog;
import org.nightlabs.base.util.GeomUtil;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.j2dswt.DrawComponentPaintable;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class J2DPrintDialog 
extends CenteredDialog 
{
	public static final Logger LOGGER = Logger.getLogger(J2DPrintDialog.class);
	
	public J2DPrintDialog(Shell parentShell, DrawComponent dc, PageFormat pageFormat) 
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);				
		this.drawComponent = dc;
		this.pageFormat = pageFormat;
		init();
	}

	protected void configureShell(Shell newShell) 
	{
		super.configureShell(newShell);		
		newShell.setText(EditorPlugin.getResourceString("dialog.print.title"));
		newShell.setSize(350, 325);		
	}	
	
	protected PageFormat pageFormat; 
	public PageFormat getPageFormat() {
		return pageFormat;
	}
	protected DrawComponent drawComponent;
	protected J2DCanvas canvas;
	protected IPaintable paintable;
		
//	protected Button fitPage;
//	protected Button fitWidth;
//	protected Button fitHeight;
//	
//	protected int fit = FIT_PAGE;
//	public static final int FIT_PAGE = 1;
//	public static final int FIT_HEIGHT = 2;
//	public static final int FIT_WIDTH = 3;
//	
//	protected SelectionListener fitPageSelectionListener = new SelectionListener()
//	{	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}	
//		public void widgetSelected(SelectionEvent e) {
//			fit = FIT_PAGE;
//			refresh();
//		}	
//	};
//
//	protected SelectionListener fitWidthSelectionListener = new SelectionListener()
//	{	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}	
//		public void widgetSelected(SelectionEvent e) {
//			fit = FIT_WIDTH;
//			refresh();
//		}	
//	};	
//
//	protected SelectionListener fitHeightSelectionListener = new SelectionListener()
//	{	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}	
//		public void widgetSelected(SelectionEvent e) {
//			fit = FIT_HEIGHT;
//			refresh();
//		}	
//	};	
	
	protected Button alignHorizontal;
	protected Button alignVertical;

	protected int align = HORIZONTAL_ALIGNMENT;
	public static final int HORIZONTAL_ALIGNMENT = PageFormat.LANDSCAPE;
	public static final int VERTICAL_ALIGNMENT = PageFormat.PORTRAIT;
	
	protected SelectionListener alignVerticalListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			align = VERTICAL_ALIGNMENT;
			refresh();
		}	
	};		

	protected SelectionListener alignHorizontalListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			align = HORIZONTAL_ALIGNMENT;
			refresh();
		}	
	};		
		
//	protected Text marginTopWidget;
//	protected Text marginBottomWidget;
//	protected Text marginLeftWidget;
//	protected Text marginRightWidget;
	protected Spinner marginTopWidget;
	protected Spinner marginBottomWidget;
	protected Spinner marginLeftWidget;
	protected Spinner marginRightWidget;
	
	protected double marginTop;
	protected double marginBottom;
	protected double marginLeft;
	protected double marginRight;
	
//	protected SelectionListener marginListener = new SelectionAdapter()
//	{	
//		public void widgetSelected(SelectionEvent e) 
//		{
//			Text t = (Text) e.getSource(); 
//			String text = t.getText();
//			try {
//				java.lang.Double d = java.lang.Double.parseDouble(text);
//				double value = d.doubleValue();
//				if (t.equals(marginTopWidget))
//					setMarginValue(SWT.TOP, value);
//				if (t.equals(marginBottomWidget))
//					setMarginValue(SWT.BOTTOM, value);
//				if (t.equals(marginLeft))
//					setMarginValue(SWT.LEFT, value);
//				if (t.equals(marginRight))
//					setMarginValue(SWT.RIGHT, value);				
//			} catch (NumberFormatException nfe) {
//				
//			}
//			refresh();
//		}	
//	};
	protected SelectionListener marginListener = new SelectionAdapter()
	{	
		public void widgetSelected(SelectionEvent e) 
		{
			Spinner t = (Spinner) e.getSource(); 
			int value = t.getSelection();
			if (t.equals(marginTopWidget))
				setMarginValue(SWT.TOP, value);
			if (t.equals(marginBottomWidget))
				setMarginValue(SWT.BOTTOM, value);
			if (t.equals(marginLeftWidget))
				setMarginValue(SWT.LEFT, value);
			if (t.equals(marginRightWidget))
				setMarginValue(SWT.RIGHT, value);	
			
			refresh();
		}	
	};	
				
	protected void setMarginValue(int orientation, double value) 
	{
		Paper paper = pageFormat.getPaper();		
		switch (orientation) 
		{		
			case(SWT.TOP):
				marginTop = value;
				break;
			case(SWT.LEFT):
				marginLeft = value;
				break;
			case(SWT.RIGHT):
				marginRight = value;
				break;
			case(SWT.BOTTOM):
				marginBottom = value;
				break;				
		}		
		paper.setImageableArea(marginLeft, marginTop, (paper.getWidth() - (marginLeft + marginRight)), 
				(paper.getHeight() - (marginTop + marginBottom)) );		
		pageFormat.setPaper(paper);		
	}	
	
//	protected void setMarginValue(int position, double value) 
//	{
//		Paper paper = pageFormat.getPaper();		
//		switch (position) 
//		{		
//			case(SWT.TOP):
//				marginTop = value;
//				break;
//			case(SWT.LEFT):
//				marginLeft = value;
//				break;
//			case(SWT.RIGHT):
//				marginRight = value;
//				break;
//			case(SWT.BOTTOM):
//				marginBottom = value;
//				break;				
//		}
//		if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
//			paper.setImageableArea(marginLeft, marginTop, (paper.getWidth() - (marginLeft + marginRight)), 
//					(paper.getHeight() - (marginTop + marginBottom)) );					
//		} 
//		else if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
//			paper.setImageableArea(marginBottom, marginLeft, (paper.getWidth() - (marginBottom + marginTop)), 
//					(paper.getHeight() - (marginLeft + marginRight)) );
//		}
//		pageFormat.setPaper(paper);		
//	}		
	
	protected Control createDialogArea(Composite parent) 
	{
		Composite comp = new XComposite(parent, SWT.NONE);		
		comp.setLayout(new GridLayout(2, false));
		
//		Composite prefComp = new XComposite(comp, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		Composite prefComp = new Composite(comp, SWT.NONE);		
		prefComp.setLayout(new GridLayout(1, true));
//		prefComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
//		// Fit		
//		Group fitGroup = new Group(prefComp, SWT.NONE);
//		fitGroup.setText(EditorPlugin.getResourceString("dialog.print.detailGroup.title"));
//		fitGroup.setLayout(new GridLayout(1, true));				
//		GridData fitData = new GridData();
//		fitData.verticalAlignment = SWT.BEGINNING;
//		fitData.horizontalAlignment = SWT.FILL;
//		fitGroup.setLayoutData(fitData);
//		
//		fitPage = new Button(fitGroup, SWT.RADIO);
//		fitPage.setText(EditorPlugin.getResourceString("dialog.print.buttonFitPage.text"));
//		fitPage.addSelectionListener(fitPageSelectionListener);
//
//		fitWidth = new Button(fitGroup, SWT.RADIO);
//		fitWidth.setText(EditorPlugin.getResourceString("dialog.print.buttonFitWidth.text"));
//		fitWidth.addSelectionListener(fitWidthSelectionListener);
//
//		fitHeight = new Button(fitGroup, SWT.RADIO);
//		fitHeight.setText(EditorPlugin.getResourceString("dialog.print.buttonFitHeight.text"));
//		fitHeight.addSelectionListener(fitHeightSelectionListener);

		// Alignement
		Group alignmentGroup = new Group(prefComp, SWT.NONE);
		alignmentGroup.setText(EditorPlugin.getResourceString("dialog.print.alignmentGroup.text"));
		alignmentGroup.setLayout(new GridLayout(1, true));
		GridData alignData = new GridData();
//		alignData.verticalAlignment = SWT.CENTER;		
		alignData.verticalAlignment = SWT.BEGINNING;		
		alignData.horizontalAlignment = SWT.FILL;
		alignmentGroup.setLayoutData(alignData);
		
		alignHorizontal = new Button(alignmentGroup, SWT.RADIO);
		alignHorizontal.setText(EditorPlugin.getResourceString("dialog.print.buttonAlignHorizontal.text"));
		alignVertical = new Button(alignmentGroup, SWT.RADIO);
		alignVertical.setText(EditorPlugin.getResourceString("dialog.print.buttonAlignVertical.text"));		
		
		if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
			alignHorizontal.setSelection(true);
			align = HORIZONTAL_ALIGNMENT;
		}
		if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
			align = VERTICAL_ALIGNMENT;
			alignVertical.setSelection(true);			
		}
		
		alignHorizontal.addSelectionListener(alignHorizontalListener);		
		alignVertical.addSelectionListener(alignVerticalListener);		
		
		// Margins
		Group marginsGroup = new Group(prefComp, SWT.NONE);
		marginsGroup.setLayout(new GridLayout(1, true));
		marginsGroup.setText(EditorPlugin.getResourceString("dialog.print.marginsGroup.text"));
		marginsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createMarginsEntry(marginsGroup, SWT.TOP, 
				EditorPlugin.getResourceString("dialog.print.labelMarginTop.text"), marginTop);
		createMarginsEntry(marginsGroup, SWT.BOTTOM, 
				EditorPlugin.getResourceString("dialog.print.labelMarginBottom.text"), marginBottom);
		createMarginsEntry(marginsGroup, SWT.LEFT, 
				EditorPlugin.getResourceString("dialog.print.labelMarginLeft.text"), marginLeft);
		createMarginsEntry(marginsGroup, SWT.RIGHT, 
				EditorPlugin.getResourceString("dialog.print.labelMarginRight.text"), marginRight);		
				
		// Preview 
		Composite canvasComp = new XComposite(comp, SWT.BORDER);
		GridLayout canvasLayout = new GridLayout();
		canvasComp.setLayout(canvasLayout);
		canvasComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		canvas = new J2DCanvas(canvasComp, SWT.NONE, paintable);
		canvas.setBackground(new Color(comp.getDisplay(), 255, 255, 255));
		canvas.addControlListener(canvasResizeListener);		
		canvas.addPaintListener(canvasPaintListener);	
		
		refresh();
		
		return comp;
	}	
		
	protected Composite createMarginsEntry(Composite parent, int orientation, String label, double value) 
	{
		Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;		
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Spinner t = new Spinner(comp, SWT.BORDER);
//		Text t = new Text(comp, SWT.BORDER);		
		switch (orientation) {
			case(SWT.TOP):
				marginTopWidget = t;
			case(SWT.BOTTOM):
				marginBottomWidget = t;
			case(SWT.LEFT):
				marginLeftWidget = t;
			case(SWT.RIGHT):
				marginRightWidget = t;
		}		
		t.setSelection((int)Math.rint(value));
		t.addSelectionListener(marginListener);
		Label l = new Label(comp, SWT.NONE);
		l.setText(label);
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return comp;		
	}
	
	protected void setCanvasZoom(double scale) {
		canvas.getPaintableManager().setScale(scale);		
	}

	protected void updateCanvas() {
		canvas.repaint();	
	}
		
	protected ControlListener canvasResizeListener = new ControlAdapter() {	
		public void controlResized(ControlEvent e) {
//			refresh();
		}	
	};
		
	protected Rectangle pageRectangle;
	protected Rectangle imageablePageRectangle;
			
	protected void initPage(PageFormat pf) 
	{
		pageRectangle = new Rectangle(0, 0, (int)pf.getWidth(), (int)pf.getHeight());
		imageablePageRectangle = new Rectangle(
				(int)pf.getImageableX(), (int)pf.getImageableY(), 
				(int)pf.getImageableWidth(), (int)pf.getImageableHeight());

		double newMarginTop = Math.rint(pf.getImageableY());
		double newMarginBottom = Math.rint((pf.getHeight() - (pf.getImageableY() + pf.getImageableHeight())));
		double newMarginLeft = Math.rint(pf.getImageableX());
		double newMarginRight = Math.rint((pf.getWidth() - (pf.getImageableX() + pf.getImageableWidth())));			

		if (pf.getOrientation() == PageFormat.PORTRAIT) {
			marginTop = newMarginTop;
			marginBottom = newMarginBottom;
			marginLeft = newMarginLeft;
			marginRight = newMarginRight;			
		}
		else if (pf.getOrientation() == PageFormat.LANDSCAPE) {
			marginTop = newMarginLeft;
			marginBottom = newMarginRight;
			marginLeft = newMarginBottom;
			marginRight = newMarginTop;			
		}
										
		if (marginTopWidget != null)
			marginTopWidget.setSelection((int)marginTop);
		if (marginBottomWidget != null)
			marginBottomWidget.setSelection((int)marginBottom);
		if (marginLeftWidget != null)
			marginLeftWidget.setSelection((int)marginLeft);
		if (marginRightWidget != null)
			marginRightWidget.setSelection((int)marginRight);
			
		LOGGER.debug("initPage");
		PrintUtil.logPageFormat(pf);
		LOGGER.debug("marginTop = " + marginTop);
		LOGGER.debug("marginBottom = " + marginBottom);
		LOGGER.debug("marginLeft = " + marginLeft);
		LOGGER.debug("marginRight = " + marginRight);		
	}	
		
//	protected void initPage(PageFormat pf) 
//	{
//		pageRectangle = new Rectangle(0, 0, (int)pf.getWidth(), (int)pf.getHeight());
//		imageablePageRectangle = new Rectangle(
//				(int)pf.getImageableX(), (int)pf.getImageableY(), 
//				(int)pf.getImageableWidth(), (int)pf.getImageableHeight());
//
//		marginTop = Math.rint(pf.getImageableY());
//		marginBottom = Math.rint((pf.getHeight() - (pf.getImageableY() + pf.getImageableHeight())));
//		marginLeft = Math.rint(pf.getImageableX());
//		marginRight = Math.rint((pf.getWidth() - (pf.getImageableX() + pf.getImageableWidth())));		
//										
//		if (marginTopWidget != null)
//			marginTopWidget.setSelection((int)marginTop);
//		if (marginBottomWidget != null)
//			marginBottomWidget.setSelection((int)marginBottom);
//		if (marginLeftWidget != null)
//			marginLeftWidget.setSelection((int)marginLeft);
//		if (marginRightWidget != null)
//			marginRightWidget.setSelection((int)marginRight);
//			
//		LOGGER.debug("initPage");
//		PrintUtil.logPageFormat(pf);
//		LOGGER.debug("marginTop = " + marginTop);
//		LOGGER.debug("marginBottom = " + marginBottom);
//		LOGGER.debug("marginLeft = " + marginLeft);
//		LOGGER.debug("marginRight = " + marginRight);		
//	}		
	
	protected Rectangle dcBounds;
	protected Rectangle canvasBounds;
	
	protected double scale = 1;
//	protected double scaleX = 1;
//	protected double scaleY = 1;
		
	protected double canvasScaleFactor = 4;
	protected void setCanvasSize() 
	{
		GridData canvasData = new GridData();
		int newWidth = (int) Math.rint((double)pageRectangle.width / canvasScaleFactor);
		int newHeight = (int) Math.rint((double)pageRectangle.height / canvasScaleFactor);		
		canvasData.widthHint = newWidth;
		canvasData.heightHint = newHeight; 
		canvas.setLayoutData(canvasData);
		canvas.setSize(newWidth, newHeight);						
	}
				
	protected void refresh() 
	{		
		LOGGER.debug("refresh");
		
		switch (align) {
			case(HORIZONTAL_ALIGNMENT):
				pageFormat.setOrientation(PageFormat.LANDSCAPE);
				break;
			case(VERTICAL_ALIGNMENT):
				pageFormat.setOrientation(PageFormat.PORTRAIT);
				break;				
		}
		initPage(pageFormat);	
		setCanvasSize();
		
		dcBounds = drawComponent.getBounds();
		dcBounds = GeomUtil.translateRectToOrigin(dcBounds);				
		canvasBounds = GeomUtil.toAWTRectangle(canvas.getClientArea());
		translateCanvas();
		
//		double shrinkX = translateX;
//		double shrinkY = translateY;
//		if ((marginLeft + marginRight) != 0)
//			shrinkX = (marginLeft + marginRight) / canvasScaleFactor;
//		if ((marginTop + marginBottom) != 0)
//			shrinkY = (marginTop + marginBottom) / canvasScaleFactor;		
//		Rectangle shrinkedCanvasBounds = shrinkRect(canvasBounds, (int)shrinkX, (int)shrinkY);
		Rectangle shrinkedCanvasBounds = shrinkRect(canvasBounds, (int)translateX, (int)translateY);
		
		Point2D scales = calcScale(dcBounds, shrinkedCanvasBounds);						
		scale = Math.min(scales.getX(), scales.getY());
		
		LOGGER.debug("dcBounds = " + dcBounds);
		LOGGER.debug("canvasBounds = " + canvasBounds);
		LOGGER.debug("imageablePageRectangle = " + imageablePageRectangle);
		LOGGER.debug("scale = "+scale);
		LOGGER.debug("translateX = "+translateX);		
		LOGGER.debug("translateY = "+translateY);
		LOGGER.debug("");
				
		setCanvasZoom(scale);		
		updateCanvas();
	}			
	
	protected Point2D calcScale(Rectangle r1, Rectangle r2) 
	{
		double scaleX = 1.0;
		double scaleY = 1.0;
		if (r1.width != 0 && r2.width != 0)
			scaleX = (double)r2.width / (double)r1.width;
		if (r1.height != 0 && r2.height != 0)
			scaleY = (double)r2.height / (double)r1.height;
		
		return new Point2D.Double(scaleX, scaleY);
	}
	
	protected double translateX = 0; 
	protected double translateY = 0;
	protected void translateCanvas() 
	{
		AffineTransform initalAT = canvas.getPaintableManager().getInitialTransform();
		AffineTransform at = new AffineTransform(initalAT);
		translateX = (double)pageFormat.getImageableX() / canvasScaleFactor;
		translateY = (double)pageFormat.getImageableY() / canvasScaleFactor;
		at.setToTranslation(translateX, translateY);		
		canvas.getPaintableManager().setInitialTransform(at);		
	}
		
	protected PaintListener canvasPaintListener = new PaintListener()
	{	
		public void paintControl(PaintEvent e) 
		{
			GC gc = e.gc; 
			Transform transform = new Transform(gc.getDevice());			
			Rectangle canvasBounds = GeomUtil.toAWTRectangle(canvas.getClientArea());			
			Point2D scales = calcScale(pageRectangle, canvasBounds);			
			double gcScale = Math.min(scales.getX(), scales.getY());											
			transform.scale((float)gcScale, (float)gcScale);
			
			gc.setTransform(transform);			
			gc.setLineWidth(3);
			gc.setLineStyle(SWT.LINE_DASH);
			gc.drawRectangle(imageablePageRectangle.x, imageablePageRectangle.y, 
					imageablePageRectangle.width, imageablePageRectangle.height);						
		}	
	};
			
	protected void init() 
	{
//	paintable = new PrintPaintable(drawComponent, pageFormat);
		paintable = new DrawComponentPaintable(drawComponent);		
		initPage(pageFormat);		
	}
	
	protected Rectangle shrinkRect(Rectangle r, int diffX, int diffY) 
	{
		Rectangle rect = new Rectangle(r);
		rect.grow(-diffX, -diffY);
		return rect;
	}
}
