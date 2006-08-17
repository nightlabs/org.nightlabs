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

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.util.GeomUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PrintPreviewComposite 
extends XComposite 
{
	private static final Logger logger = Logger.getLogger(PrintPreviewComposite.class);
	
	/**
	 * @param parent
	 * @param style
	 */
	public PrintPreviewComposite(PageFormat pageFormat, Composite parent, int style) {
		super(parent, style);
		init(pageFormat);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public PrintPreviewComposite(PageFormat pageFormat, Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		init(pageFormat);
	}

	protected void init(PageFormat pf) 
	{
		if (pf == null)
			throw new IllegalArgumentException("Param pageFormat must NOT be null!");
		
		this.pageFormat = pf;
		initPage(pf);
		createComposite(this);
		addDisposeListener(disposeListener);
	}
	
	protected void initPage(PageFormat pf) 
	{
		pageRectangle = new Rectangle(0, 0, (int)pf.getWidth(), (int)pf.getHeight());
		imageablePageRectangle = new Rectangle(
				(int)pf.getImageableX(), (int)pf.getImageableY(), 
				(int)pf.getImageableWidth(), (int)pf.getImageableHeight());

		marginTop = Math.rint(pf.getImageableY());
		marginBottom = Math.rint((pf.getHeight() - (pf.getImageableY() + pf.getImageableHeight())));
		marginLeft = Math.rint(pf.getImageableX());
		marginRight = Math.rint((pf.getWidth() - (pf.getImageableX() + pf.getImageableWidth())));												
	}	
	
	private double marginTop = 0;
	public void setMarginTop(double marginTop) {
		this.marginTop = marginTop;
		setMarginValue(SWT.TOP, marginTop);		
	}
	public double getMarginTop() {
		return marginTop;
	}
	
	private double marginBottom = 0;
	public double getMarginBottom() {
		return marginBottom;
	}
	public void setMarginBottom(double marginBottom) {
		this.marginBottom = marginBottom;
		setMarginValue(SWT.BOTTOM, marginBottom);		
	}	
	
	private double marginLeft = 0;
	public double getMarginLeft() {
		return marginLeft;
	}
	public void setMarginLeft(double marginLeft) {
		this.marginLeft = marginLeft;
		setMarginValue(SWT.LEFT, marginLeft);		
	}
	
	private double marginRight = 0; 
	public double getMarginRight() {
		return marginRight;
	}
	public void setMarginRight(double marginRight) {
		this.marginRight = marginRight;
		setMarginValue(SWT.RIGHT, marginRight);		
	}	
	
	private PageFormat pageFormat = null;
	public PageFormat getPageFormat() {
		return pageFormat;
	}
	public void setPageFormat(PageFormat pageFormat) {
		this.pageFormat = pageFormat;
		initPage(pageFormat);
		updateCanvas();
	}
	
	protected void setMarginValue(int position, double value) 
	{
		Paper paper = pageFormat.getPaper();		
		switch (position) 
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
		if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
			paper.setImageableArea(marginLeft, marginTop, (paper.getWidth() - (marginLeft + marginRight)), 
					(paper.getHeight() - (marginTop + marginBottom)) );					
		} 
		else if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
			paper.setImageableArea(marginTop, marginRight, (paper.getWidth() - (marginBottom + marginTop)), 
					(paper.getHeight() - (marginLeft + marginRight)) );						
		}
		pageFormat.setPaper(paper);
		initPage(pageFormat);	
		updateCanvas();
	}		
		
	private Canvas canvas = null;
	protected Canvas initCanvas(Composite parent) 
	{
		Canvas c = new Canvas(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		return c;
	}
	
	protected void createComposite(Composite parent) 
	{
		canvas = initCanvas(parent);
//		canvas.setBackground(new Color(parent.getDisplay(), 255, 255, 255));		
		canvas.addControlListener(canvasResizeListener);		
		canvas.addPaintListener(pagePaintListener);
		updateCanvas();
	}
	
	private Rectangle pageRectangle = null;
	private Rectangle imageablePageRectangle = null;	
	private Color colorWhite = new Color(Display.getDefault(), 255, 255, 255);
	private PaintListener pagePaintListener = new PaintListener()
	{	
		public void paintControl(PaintEvent e) 
		{
			GC gc = e.gc; 
			Transform transform = new Transform(gc.getDevice());			
			Rectangle canvasBounds = org.nightlabs.base.util.GeomUtil.toAWTRectangle(canvas.getClientArea());			
			Point2D scales = GeomUtil.calcScale(pageRectangle, canvasBounds);			
			double gcScale = Math.min(scales.getX(), scales.getY());											
			transform.scale((float)gcScale, (float)gcScale);
			
			gc.setTransform(transform);			
			
			gc.setBackground(colorWhite);
			gc.fillRectangle(pageRectangle.x, pageRectangle.y, 
					pageRectangle.width, pageRectangle.height);			
			
			gc.setLineWidth(3);
			gc.setLineStyle(SWT.LINE_SOLID);			
			gc.drawRectangle(imageablePageRectangle.x, imageablePageRectangle.y, 
					imageablePageRectangle.width, imageablePageRectangle.height);						
		}	
	};

	private ControlListener canvasResizeListener = new ControlAdapter() 
	{	
		public void controlResized(ControlEvent e) 
		{

		}	
	};
	
	private DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) {
			canvas.removePaintListener(pagePaintListener);
			canvas.removeControlListener(canvasResizeListener);			
		}	
	};
		
	protected void updateCanvas() 
	{
		canvas.redraw();
//		redraw();		
//		if (logger.isDebugEnabled())
//			logger.debug("updateCanvas()");
	}
	
}
