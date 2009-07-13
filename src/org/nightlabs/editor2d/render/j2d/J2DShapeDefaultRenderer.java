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
import java.awt.Graphics2D;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;


public class J2DShapeDefaultRenderer
extends J2DBaseShapeRenderer
{
	/**
	 * This Implementation of a ShapeDrawComponent-Renderer,
	 * draws the GeneralShape of the ShapeDrawComponent.
	 * The Stroke is filled with the lineColor if
	 * {@link ShapeDrawComponent#isShowStroke()} is true and respectively
	 * the interior is filled with the fillColor if {@link ShapeDrawComponent#isFill()}
	 * is true
	 * 
	 * @see J2DRenderContext#paint(DrawComponent)
	 * @see ShapeDrawComponent
	 */
	@Override
	public void paint(DrawComponent dc, Graphics2D g2d)
	{
		ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
		if (sdc.isFill()) {
			g2d.setPaint(getFillColor(sdc));
			if (sdc.getGeneralShape() != null)
				g2d.fill(sdc.getGeneralShape());
		}
		if (sdc.isShowStroke()) {
			g2d.setPaint(getLineColor(sdc));
			g2d.setStroke(sdc.getStroke());
			if (sdc.getGeneralShape() != null)
				g2d.draw(sdc.getGeneralShape());
		}

		if (sdc.isTemplate()) {
			g2d.setPaint(TEMPLATE_COLOR);
			g2d.fill(sdc.getGeneralShape());
		}
	}

	public static final Color TEMPLATE_COLOR = new Color(
			Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), 200);

	protected Color getFillColor(ShapeDrawComponent sdc) {
		return sdc.getFillColor();
	}

	protected Color getLineColor(ShapeDrawComponent sdc) {
		return sdc.getLineColor();
	}
	
}
