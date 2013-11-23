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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class J2DPageRenderer
extends J2DBaseRenderer
{
	public static final Stroke STROKE = new BasicStroke(1);
	
	public J2DPageRenderer() {
		super();
	}

  @Override
	public void paint(DrawComponent dc, Graphics2D g2d)
  {
  	if (dc instanceof PageDrawComponent)
  	{
  		PageDrawComponent pdc = (PageDrawComponent) dc;
  		if (pdc.isShowPageBounds()) {
    		g2d.setPaint(Color.GRAY);
    		g2d.setStroke(STROKE);
    		g2d.drawRect(pdc.getPageBounds().x, pdc.getPageBounds().y,
    				pdc.getPageBounds().width, pdc.getPageBounds().height);
  		}
  	}
  	super.paint(dc, g2d);
  }

}
