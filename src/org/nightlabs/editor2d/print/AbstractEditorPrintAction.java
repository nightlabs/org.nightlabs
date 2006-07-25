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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import org.apache.log4j.Logger;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.actions.AbstractEditorAction;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class AbstractEditorPrintAction 
extends AbstractEditorAction 
{
	public static final Logger LOGGER = Logger.getLogger(AbstractEditorPrintAction.class);
	
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
		PrinterData[] printers = Printer.getPrinterList();
		return printers != null && printers.length > 0;
	}

	protected PageFormat getPageFormat() {
		return getEditor().getPageFormat();
	}	
	protected void setPageFormat(PageFormat pageFormat) {		
		getEditor().setPageFormat(pageFormat);
	}
	
	protected J2DRenderContext j2drc = null;
	protected Printable printable = new Printable()
	{	
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) 
		throws PrinterException 
		{
			Graphics2D g2d = (Graphics2D) graphics;
			Renderer r = getDrawComponent().getRenderer();
			if (r.getRenderContext() instanceof J2DRenderContext)
				j2drc = (J2DRenderContext) r.getRenderContext();
			else
				j2drc = (J2DRenderContext) r.getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);
						
			// Print only 1 Page
			if (pageIndex >= 1) {
        return Printable.NO_SUCH_PAGE;
			}
			if (j2drc != null) {
				prepareGraphics(g2d, getDrawComponent(), pageFormat);				
				j2drc.paint(getDrawComponent(), g2d);
				return Printable.PAGE_EXISTS;
			}
			return Printable.NO_SUCH_PAGE;
		}	
	};	
	
	public static void prepareGraphics(Graphics2D g2d, DrawComponent dc, PageFormat pageFormat) 
	{		
		long startTime = System.currentTimeMillis();
		PrintUtil.prepareGraphics(g2d, dc, pageFormat);
		long endTime = System.currentTimeMillis() - startTime;
		LOGGER.debug("prepareGraphics took "+endTime+" ms!");
	}
	
	public DrawComponent getDrawComponent() {
		return getMultiLayerDrawComponent();
	}
}
