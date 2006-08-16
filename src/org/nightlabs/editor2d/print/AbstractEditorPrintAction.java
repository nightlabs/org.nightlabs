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

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;
import org.nightlabs.base.print.PrinterInterfaceManager;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.actions.AbstractEditorAction;
import org.nightlabs.editor2d.print.EditorPrintable.PrintConstant;
import org.nightlabs.print.AWTPrinter;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterConfigurationCfMod;
import org.nightlabs.print.PrinterInterface;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class AbstractEditorPrintAction 
extends AbstractEditorAction 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractEditorPrintAction.class);
	
	/**
	 * @param editor
	 * @param style
	 */
	public AbstractEditorPrintAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public AbstractEditorPrintAction(AbstractEditor editor) {
		super(editor);
	}

	/**
	 * returns true if there are printers available otherwise returns false
	 * 
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() 
	{
		// Changed to Swing way of getting a printer list, 
		// as this system is acutally used for printing.
		PrintService[] pServices = PrintServiceLookup.lookupPrintServices(null, null);
		return pServices != null && pServices.length > 0;
		
//		PrinterData[] printers = Printer.getPrinterList();
//		return printers != null && printers.length > 0;
	}

	protected AWTPrinter getAWTPrinter() 
	{
		PrinterInterface printer;
		try {
			printer = PrinterInterfaceManager.sharedInstance().getConfiguredPrinterInterface(
					PrinterInterfaceManager.INTERFACE_FACTORY_AWT,
					PrintUtil.PRINTER_USE_CASE_EDITOR_2D 
				);
		} catch (PrinterException e) {
			throw new RuntimeException(e);
		}
		return (AWTPrinter) printer;
	}
	
//	protected PageFormat getPageFormat() {
//		return getEditor().getPageFormat();
//	}	
//	protected void setPageFormat(PageFormat pageFormat) {		
//		getEditor().setPageFormat(pageFormat);
//	}
	
	public Printable getPrintable(PrintConstant printConstant) 
	{
		switch (printConstant) {
			case FIT_ALL:
				return new EditorPrintable(getDrawComponent(), printConstant, 1);
			case FIT_PAGE:
				return new EditorPrintable(getDrawComponent(), printConstant, 1);				
		}
		return new EditorPrintable(getDrawComponent(), printConstant, 1);		
	}
	
	public DrawComponent getDrawComponent() {
		return getMultiLayerDrawComponent();
	}
	
//	protected J2DRenderContext j2drc = null;
//	private Printable dcPrintable = new Printable()
//	{	
//		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) 
//		throws PrinterException 
//		{
//			Graphics2D g2d = (Graphics2D) graphics;
//			Renderer r = getDrawComponent().getRenderer();
//			if (r.getRenderContext() instanceof J2DRenderContext)
//				j2drc = (J2DRenderContext) r.getRenderContext();
//			else
//				j2drc = (J2DRenderContext) r.getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);
//						
//			// Print only 1 Page
//			if (pageIndex >= 1) {
//        return Printable.NO_SUCH_PAGE;
//			}
//			if (j2drc != null) {
//				prepareGraphics(g2d, getDrawComponent(), pageFormat);				
//				j2drc.paint(getDrawComponent(), g2d);
//				return Printable.PAGE_EXISTS;
//			}
//			return Printable.NO_SUCH_PAGE;
//		}	
//	};	
//
//	private Printable pagePrintable = new Printable()
//	{	
//		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) 
//		throws PrinterException 
//		{
//			Graphics2D g2d = (Graphics2D) graphics;
//			Renderer r = getPageDrawComponent().getRenderer();
//			if (r.getRenderContext() instanceof J2DRenderContext)
//				j2drc = (J2DRenderContext) r.getRenderContext();
//			else
//				j2drc = (J2DRenderContext) r.getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);
//						
//			// Print only 1 Page
//			if (pageIndex >= 1) {
//        return Printable.NO_SUCH_PAGE;
//			}
//			if (j2drc != null) {
//				prepareGraphics(g2d, getPageDrawComponent(), pageFormat);				
//				j2drc.paint(getPageDrawComponent(), g2d);
//				return Printable.PAGE_EXISTS;
//			}
//			return Printable.NO_SUCH_PAGE;
//		}	
//	};	
			
//	public static void prepareGraphics(Graphics2D g2d, DrawComponent dc, PageFormat pageFormat) 
//	{		
//		long startTime = System.currentTimeMillis();
//		PrintUtil.prepareGraphics(g2d, dc, pageFormat);
//		long endTime = System.currentTimeMillis() - startTime;
//		logger.debug("prepareGraphics took "+endTime+" ms!");
//	}
//	
//	public static void prepareGraphics(Graphics2D g2d, PageDrawComponent page, PageFormat pageFormat) 
//	{		
//		long startTime = System.currentTimeMillis();
//		PrintUtil.prepareGraphics(g2d, page, pageFormat);		
//		long endTime = System.currentTimeMillis() - startTime;
//		logger.debug("prepareGraphics took "+endTime+" ms!");
//	}
//		
//	public PageDrawComponent getPageDrawComponent() {
//		return getMultiLayerDrawComponent().getCurrentPage();
//	}
	
}
