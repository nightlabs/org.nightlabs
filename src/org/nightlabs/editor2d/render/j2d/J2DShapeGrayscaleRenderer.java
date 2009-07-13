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
package org.nightlabs.editor2d.render.j2d;

import java.awt.Color;

import org.nightlabs.editor2d.ShapeDrawComponent;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class J2DShapeGrayscaleRenderer
extends J2DShapeDefaultRenderer
{	
//  protected static ColorSpace colorSpaceGrey = ImageUtil.COLOR_MODEL_GRAY.getColorSpace();
//
//	@Override
//	protected Color getFillColor(ShapeDrawComponent sdc) {
//		return RenderUtil.convertColor(sdc.getFillColor(), colorSpaceGrey);
//	}
//
//	@Override
//	protected Color getLineColor(ShapeDrawComponent sdc) {
//		return RenderUtil.convertColor(sdc.getLineColor(), colorSpaceGrey);
//	}

	@Override
	protected Color getFillColor(ShapeDrawComponent sdc) {
		return convertColorToGrey(sdc.getFillColor());
	}

	@Override
	protected Color getLineColor(ShapeDrawComponent sdc) {
		return convertColorToGrey(sdc.getLineColor());
	}
  
	public static Color convertColorToGrey(Color old)
	{
    int newRed = (int) (0.5 * old.getRed()  + 0.3 * old.getGreen() + 0.2 * old.getBlue());
    int newGreen = (int) (0.5 * old.getRed()  + 0.3 * old.getGreen() + 0.2 * old.getBlue());
    int newBlue = (int) (0.5 * old.getRed()  + 0.3 * old.getGreen() + 0.2 * old.getBlue());
    return new Color(newRed, newGreen, newBlue, old.getAlpha());
	}
}
