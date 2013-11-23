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

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.RenderContext;

/**
 * The Base Interface for RenderContext of the Type Java2D,
 * means rendering occurs on an Graphics2D
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface J2DRenderContext
extends RenderContext<Graphics2D>
{
	public static final String RENDER_CONTEXT_TYPE_JAVA2D = "Java2D";
	
	/**
	 * paints the content of the drawComponent on the Graphics2D
	 * 
	 * @param dc the drawComponent to draw
	 * @param g2d the Graphics2D to paint on
	 */
	void paint(DrawComponent dc, Graphics2D g2d);
}
