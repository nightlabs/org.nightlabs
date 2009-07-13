/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;
import org.nightlabs.editor2d.util.GeomUtil;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DrawComponentPrintable
implements Printable
{
	private static final Logger logger = Logger.getLogger(DrawComponentPrintable.class);
	private int totalAmountOfPages = 0;
	private Map<DrawComponent, Integer> dc2PageAmount = new HashMap<DrawComponent, Integer>();
	private Map<Integer, DrawComponent> pageIndex2DrawComponent = new HashMap<Integer, DrawComponent>();
	private PrintConstant printConstant;
	private boolean renderPageBounds = false;
	private Map<PageDrawComponent, Boolean> page2ShowPageBounds = new HashMap<PageDrawComponent, Boolean>();
	private int printerResolutionXInDPI = 0;
	private int printerResolutionYInDPI = 0;

	public static enum PrintConstant {
		/*FIT_ALL,*/ FIT_PAGE, ORIGINAL_SIZE;
	}

	public DrawComponentPrintable(List<? extends DrawComponent> drawComponents, PrintConstant printConstant)
	{
		this(drawComponents, printConstant, false);
	}

	public DrawComponentPrintable(List<? extends DrawComponent> drawComponents, PrintConstant printConstant, boolean showPageBounds)
	{
		this.printConstant = printConstant;
		this.renderPageBounds = showPageBounds;
		init(drawComponents);
	}

	public boolean isRenderPageBounds() {
		return renderPageBounds;
	}

	public void setRenderPageBounds(boolean showPageBounds) {
		this.renderPageBounds = showPageBounds;
	}

	private void init(List<? extends DrawComponent> drawComponents)
	{
		for (DrawComponent drawComponent : drawComponents)
		{
			int amountOfPages = 0;
			if (drawComponent instanceof RootDrawComponent)
			{
				RootDrawComponent root = (RootDrawComponent) drawComponent;
				for (Iterator<DrawComponent> it = root.getDrawComponents().iterator(); it.hasNext(); )
				{
					DrawComponent dc = it.next();
					if (dc instanceof PageDrawComponent) {
						PageDrawComponent page = (PageDrawComponent) dc;
						int pageIndex = totalAmountOfPages + amountOfPages;
						pageIndex2DrawComponent.put(pageIndex, page);
						amountOfPages++;
					}
				}
			}
			else {
				int pageIndex = totalAmountOfPages + amountOfPages;
				pageIndex2DrawComponent.put(pageIndex, drawComponent);
				amountOfPages = 1;
			}
			dc2PageAmount.put(drawComponent, amountOfPages);
			totalAmountOfPages = totalAmountOfPages + amountOfPages;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Number of drawComponents = "+drawComponents.size());
			logger.debug("totalAmountOfPages = "+totalAmountOfPages);
		}
	}

	protected int getPageAmount(DrawComponent dc) {
		return dc2PageAmount.get(dc);
	}

	public PrintConstant getPrintConstant() {
		return printConstant;
	}

	protected void prepareGraphics(Graphics2D g2d, Rectangle bounds, PageFormat pageFormat)
	{
		Rectangle dcBounds = GeomUtil.translateToOrigin(bounds);
		Rectangle pageRectangle = new Rectangle(0, 0,
				(int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());

		Point2D scales = GeomUtil.calcScale(dcBounds, pageRectangle);
		double scale = Math.min(scales.getX(), scales.getY());
		double translateX = ((pageFormat.getImageableX()) - (bounds.getX() * scale ));
		double translateY = ((pageFormat.getImageableY()) - (bounds.getY() * scale ));
		g2d.translate(translateX, translateY);
		g2d.scale(scale, scale);

		if (logger.isDebugEnabled()) {
			logger.debug("scale = "+scale);
			logger.debug("translateX = "+translateX);
			logger.debug("translateY = "+translateY);
			logger.debug("bounds = "+bounds);
			logger.debug("dcBounds = "+dcBounds);
			logger.debug("pageRectangle = "+pageRectangle);
		}
	}

	private static final boolean marcoTry = false;

	protected void prepareOriginalSizeGraphics(Graphics2D g2d, int pageIndex, PageFormat pageFormat)
	{
		DrawComponent dc = getDrawComponentToPrint(pageIndex);
		Resolution res = dc.getRoot().getResolution();

		pageFormat.getPaper().setImageableArea(0, 0, pageFormat.getPaper().getWidth(),
				pageFormat.getPaper().getHeight());

		if (marcoTry) {
			double modelDPI_X = res.getResolutionX(IResolutionUnit.dpiUnit);
			double modelDPI_Y = res.getResolutionY(IResolutionUnit.dpiUnit);
			double scaleX = 72d / modelDPI_X;
			double scaleY = 72d / modelDPI_Y;
			g2d.getTransform().scale(scaleX, scaleY);
		}
		else {
			// daniel
			AffineTransform oldTransform = g2d.getTransform();
			double modelDPI_X = res.getResolutionX(IResolutionUnit.dpiUnit);
			double modelDPI_Y = res.getResolutionY(IResolutionUnit.dpiUnit);
			double printerDPI_X = oldTransform.getScaleX() * 72d;
			if (printerDPI_X == 0) {
				printerDPI_X = getPrinterResolutionXInDPI();
			}
			double printerDPI_Y = oldTransform.getScaleY() * 72d;
			if (printerDPI_Y == 0) {
				printerDPI_Y = getPrinterResolutionYInDPI();
			}

			double scaleX = printerDPI_X / modelDPI_X;
			double scaleY = printerDPI_Y / modelDPI_Y;

			double[] matrix = new double[6];
			oldTransform.getMatrix(matrix);
			AffineTransform newTransform = new AffineTransform(scaleX, matrix[1], matrix[2],
					scaleY, matrix[4], matrix[5]);
			g2d.setTransform(newTransform);

			if (logger.isDebugEnabled()) {
				logger.debug("modelDPI_X = " + modelDPI_X);
				logger.debug("modelDPI_Y = " + modelDPI_Y);
				logger.debug("printerDPI_X = " + printerDPI_X);
				logger.debug("printerDPI_Y = " + printerDPI_Y);
				logger.debug("scaleX = "+scaleX);
				logger.debug("scaleY = "+scaleY);
				logger.debug("oldTransform = "+oldTransform);
				logger.debug("newTransform = "+newTransform);
				logger.debug("dc.getBounds() = "+dc.getBounds());
//				logger.debug("Thread : " + Thread.currentThread().toString(), new Exception());
			}
		} // daniel
	}

	public int getOrientation(int pageIndex)
	{
		DrawComponent dc = getDrawComponentToPrint(pageIndex);
		Rectangle bounds;
		if (dc instanceof PageDrawComponent) {
			PageDrawComponent page = (PageDrawComponent) dc;
			bounds = page.getPageBounds();
		}
		else {
			bounds = dc.getBounds();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("dc = "+dc);
			logger.debug("dc.bounds = "+bounds);
		}
		if (bounds.getWidth() / bounds.getHeight() > 1) {
			return PageFormat.LANDSCAPE;
		}
		else {
			return PageFormat.PORTRAIT;
		}
	}

	protected Rectangle getBoundsToPrint(int pageIndex)
	{
		DrawComponent dc = getDrawComponentToPrint(pageIndex);
		dc.clearBounds();
		PageDrawComponent page = getPageDrawComponent(dc);
		switch (printConstant)
		{
			case FIT_PAGE:
				return page.getPageBounds();
//			case FIT_ALL:
//				return dc.getBounds();
			case ORIGINAL_SIZE:
				return page.getPageBounds();
			default:
				throw new IllegalStateException("Unknown PrintConstant "+printConstant);
		}
	}

	private PageDrawComponent getPageDrawComponent(DrawComponent dc)
	{
		if (dc instanceof RootDrawComponent)
			return ((RootDrawComponent)dc).getCurrentPage();

		if (dc instanceof PageDrawComponent)
			return (PageDrawComponent) dc;
		else {
			DrawComponentContainer parent = dc.getParent();
			while (!(parent instanceof PageDrawComponent)) {
				parent = parent.getParent();
			}
			return (PageDrawComponent) parent;
		}
	}

	protected DrawComponent getDrawComponentToPrint(int pageIndex) {
		return pageIndex2DrawComponent.get(pageIndex);
	}

	public int getTotalAmountOfPages() {
		return totalAmountOfPages;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
	throws PrinterException
	{
		if (logger.isDebugEnabled()) {
			logger.debug("print pageIndex "+pageIndex);
			logger.debug("totalAmountOfPages = "+totalAmountOfPages);
		}

		if (pageIndex >= getTotalAmountOfPages()) {
			return Printable.NO_SUCH_PAGE;
		}

		Graphics2D g2d = (Graphics2D) graphics;
		if (logger.isDebugEnabled()) {
			logger.debug("g2d = " + g2d);
			logger.debug("g2d.hashCode = " + Integer.toHexString(System.identityHashCode(g2d)));
			logger.debug("g2d.getTransform() = " + g2d.getTransform());
			logger.debug("g2d.getTransform().hashCode = " + Integer.toHexString(System.identityHashCode(g2d.getTransform())));
		}

		Renderer r = getDrawComponentToPrint(pageIndex).getRenderer();
		J2DRenderContext j2drc = (J2DRenderContext) r.getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);

		if (j2drc != null)
		{
			if (printConstant == PrintConstant.FIT_PAGE)
				prepareGraphics(g2d, getBoundsToPrint(pageIndex), pageFormat);
			else if (printConstant == PrintConstant.ORIGINAL_SIZE)
				prepareOriginalSizeGraphics(g2d, pageIndex, pageFormat);

			doBeforePrint(pageIndex);

			long start = System.currentTimeMillis();
			j2drc.paint(getDrawComponentToPrint(pageIndex), g2d);
			long duration = System.currentTimeMillis() - start;
			logger.debug("print rendering took "+duration+" ms!");

			doAfterPrint(pageIndex);

			return Printable.PAGE_EXISTS;
		}
		return Printable.NO_SUCH_PAGE;
	}

	protected void doBeforePrint(int pageIndex)
	{
		DrawComponent dc = getDrawComponentToPrint(pageIndex);
		if (dc instanceof PageDrawComponent)
		{
			PageDrawComponent page = (PageDrawComponent) dc;
			boolean showPageBounds = page.isShowPageBounds();
			page2ShowPageBounds.put(page, showPageBounds);
			page.setShowPageBounds(renderPageBounds);
		}
	}

	protected void doAfterPrint(int pageIndex)
	{
		DrawComponent dc = getDrawComponentToPrint(pageIndex);
		if (dc instanceof PageDrawComponent)
		{
			PageDrawComponent page = (PageDrawComponent) dc;
			page.setShowPageBounds(page2ShowPageBounds.get(page));
		}
	}

	/**
	 * Returns the printerResolutionXInDPI.
	 * @return the printerResolutionXInDPI
	 */
	public int getPrinterResolutionXInDPI() {
		return printerResolutionXInDPI;
	}

	/**
	 * Sets the printerResolutionXInDPI.
	 * @param printerResolutionXInDPI the printerResolutionXInDPI to set
	 */
	public void setPrinterResolutionXInDPI(int printerResolutionXInDPI) {
		this.printerResolutionXInDPI = printerResolutionXInDPI;
	}

	/**
	 * Returns the printerResolutionYInDPI.
	 * @return the printerResolutionYInDPI
	 */
	public int getPrinterResolutionYInDPI() {
		return printerResolutionYInDPI;
	}

	/**
	 * Sets the printerResolutionYInDPI.
	 * @param printerResolutionYInDPI the printerResolutionYInDPI to set
	 */
	public void setPrinterResolutionYInDPI(int printerResolutionYInDPI) {
		this.printerResolutionYInDPI = printerResolutionYInDPI;
	}

}
