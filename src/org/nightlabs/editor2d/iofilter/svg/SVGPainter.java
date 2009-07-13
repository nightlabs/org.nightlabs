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
package org.nightlabs.editor2d.iofilter.svg;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.render.RenderContext;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class SVGPainter
{
//	private static final Logger logger = Logger.getLogger(SVGPainter.class);
	
	public SVGPainter() {
	}

	private List<DrawComponentPaintListener> listener = null;
	private List<DrawComponentPaintListener> getListener() {
		if (listener == null)
			listener = new LinkedList<DrawComponentPaintListener>();
		return listener;
	}
	
	public void addPaintListener(DrawComponentPaintListener listener) {
		getListener().add(listener);
	}
	public void removePaintListener(DrawComponentPaintListener listener) {
		getListener().remove(listener);
	}
	
	private void fireDrawComponentPainted(DrawComponent dc)
	{
		for (DrawComponentPaintListener listener : getListener()) {
			listener.drawComponentPainted(dc);
		}
	}
	
	private boolean firePaint = true;
	public boolean isFirePaint() {
		return firePaint;
	}
	public void setFirePaint(boolean firePaint) {
		this.firePaint = firePaint;
	}

	public void paintDrawComponent(DrawComponent dc, Graphics2D g2d)
	{
		if (dc instanceof DrawComponentContainer) {
			if (!dc.isVisible())
				return;
			
			for (DrawComponent drawComponent : ((DrawComponentContainer) dc).getDrawComponents()) {
				paintDrawComponent(drawComponent, g2d);
			}
		}
		else {
			paintJ2DRenderer(dc.getRenderer(), dc, g2d);
		}
	}
			
	private void paintJ2DRenderer(Renderer r, DrawComponent dc, Graphics2D g2d)
	{
		if (r != null && dc != null && g2d != null) {
			RenderContext<Graphics2D> rc = r.getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);
			rc.paint(dc, g2d);
			if (firePaint)
				fireDrawComponentPainted(dc);
		}
	}
	
}
