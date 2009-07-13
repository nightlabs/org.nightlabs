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

package org.nightlabs.editor2d.util;

import java.awt.Graphics2D;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.IVisible;
import org.nightlabs.editor2d.render.RenderContext;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;


public class RenderUtil
{
  protected RenderUtil() {
    super();
  }
    	
	/**
	 * Checks if the given DrawComponent is a
	 * DrawComponentContainer and if so, it paints all its children recursivly.
	 * If the given DrawComponent is no DrawComponentContainer it just paint
	 * the {@link J2DRenderContext} of its {@link Renderer} (this also happens to the children of a DrawComponentContainer,
	 * if these are not DrawComponentContainer themselves)
	 * if dc is a {@link IVisible} its checks if the IVisible is visible and paints it children only if it is
	 * 
	 * @param dc The DrawComponent to paint
	 * @param g2d the Graphics2D to draw on
	 * 
	 * @see org.nightlabs.editor2d.render.Renderer#getRenderContext()
	 * @see org.nightlabs.editor2d.DrawComponent#getRenderer()
	 */
	public static void paintDrawComponent(DrawComponent dc, Graphics2D g2d)
	{
		if (dc != null && !dc.isVisible())
			return;
		
		if (dc instanceof DrawComponentContainer) {
			for (DrawComponent drawComponent : ((DrawComponentContainer) dc).getDrawComponents()) {
				paintDrawComponent(drawComponent, g2d);
			}
		}
		else
			paintJ2DRenderer(dc.getRenderer(), dc, g2d);
	}
	
	/**
	 * Paint the given {@link DrawComponent} with the Java2D {@link RenderContext}.
	 * 
	 * @param r the {@link Renderer} to use
	 * @param dc the {@link DrawComponent} to paint
	 * @param g2d the {@link Graphics2D} to paint to.
	 */
	public static void paintJ2DRenderer(Renderer r, DrawComponent dc, Graphics2D g2d)
	{
		if (r != null && dc != null && g2d != null) {
			if (!dc.isVisible())
				return;
			
			RenderContext<Graphics2D> rc = r.getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);
			if (rc != null) {
				rc.paint(dc, g2d);
			}
		}
	}
	
}
