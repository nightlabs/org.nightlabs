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

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.print.PrintPreviewComposite;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.j2dswt.DrawComponentPaintable;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrintPreviewComposite 
extends PrintPreviewComposite 
{

	/**
	 * @param pageFormat
	 * @param parent
	 * @param style
	 */
	public EditorPrintPreviewComposite(DrawComponent dc, PageFormat pageFormat, Composite parent,
			int style) 
	{
		super(pageFormat, parent, style);
		init(dc);
		super.init(pageFormat);		
	}

	/**
	 * @param pageFormat
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public EditorPrintPreviewComposite(DrawComponent dc, PageFormat pageFormat, Composite parent,
			int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(pageFormat, parent, style, layoutMode, layoutDataMode);
		init(dc);
		super.init(pageFormat);
	}

	private void init(DrawComponent dc) 
	{
		if (dc == null)
			throw new IllegalArgumentException("Param dc must NOT be null!");
		
		this.dc = dc;
		paintable = new DrawComponentPaintable(dc);
	}
	
	private DrawComponent dc = null;
	private DrawComponentPaintable paintable = null;
	
	@Override
	protected Canvas initCanvas(Composite parent) {
		return new J2DCanvas(parent, paintable);
	}
	
	protected void init(PageFormat pf)  {
		
	}
}
