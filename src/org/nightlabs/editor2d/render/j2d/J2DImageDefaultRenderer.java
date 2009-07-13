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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;


public class J2DImageDefaultRenderer
extends J2DBaseRenderer
{
	private AffineTransform graphicsTransform;
	private AffineTransform at;

	/**
	 * @see J2DRenderContext#paint(DrawComponent)
	 */
	@Override
	public void paint(DrawComponent dc, Graphics2D g2d)
	{
		ImageDrawComponent image = (ImageDrawComponent) dc;
		if (image.getImage() != null)
		{
			graphicsTransform = g2d.getTransform();
			at = new AffineTransform(image.getAffineTransform());
			at.preConcatenate(graphicsTransform);
			g2d.setTransform(at);
			Rectangle bounds = image.getOriginalImageShape().getBounds();
			g2d.drawImage(image.getImage(), bounds.x, bounds.y, bounds.width, bounds.height, null);
			if (image.isTemplate()) {
				g2d.setPaint(J2DShapeDefaultRenderer.TEMPLATE_COLOR);
				g2d.fill(image.getOriginalImageShape());
			}
			g2d.setTransform(graphicsTransform);
		}
	}

}
