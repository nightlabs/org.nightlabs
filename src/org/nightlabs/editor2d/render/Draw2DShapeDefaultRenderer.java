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
package org.nightlabs.editor2d.render;

import java.awt.Shape;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.Path;
import org.nightlabs.base.util.ColorUtil;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.viewer.util.AWTSWTUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class Draw2DShapeDefaultRenderer 
extends Draw2DBaseRenderer 
{
	public static final Logger LOGGER = Logger.getLogger(Draw2DShapeDefaultRenderer.class);
	
	public Draw2DShapeDefaultRenderer() 
	{
		super();
	}

	public void paint(DrawComponent dc, Graphics g) 
	{
    ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
    Path path = convertShape(sdc.getGeneralShape());
    if (sdc.isFill()) {
      g.setBackgroundColor(ColorUtil.toSWTColor(sdc.getFillColor()));
      g.fillPath(path);
    }
    g.setForegroundColor(ColorUtil.toSWTColor(sdc.getLineColor()));
    g.setLineWidth(sdc.getLineWidth());
    g.setLineStyle(convertLineStyle(sdc.getLineStyle()));
    g.drawPath(path);  
    
    LOGGER.debug("shape painted!");
	}
	
	protected Path convertShape(Shape s) 
	{
		return AWTSWTUtil.convertShape(s, null, null);
	}
	 
	protected int convertLineStyle(int lineStyle) 
	{
		return lineStyle;
	}
}
