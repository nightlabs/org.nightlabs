/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.figures;

import java.awt.Graphics2D;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.J2DGraphics;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.Renderer;


public class FreeformLayer 
extends OversizedBufferFreeformLayer 
implements RendererFigure
{
	/**
	 * @param editPart
	 */
	public FreeformLayer() {
		super();
	}
	
	protected J2DGraphics j2d;
	protected Graphics2D g2d;
	public void paint(Graphics graphics) 
	{
		if (graphics instanceof J2DGraphics) {
			j2d = (J2DGraphics) graphics;
			g2d = j2d.createGraphics2D();
			g2d.setClip(null);
			renderer.paint(drawComponent, g2d);
			g2d.dispose();
		}
		paintChildren(graphics);
		//    super.paint(graphics);
	}  
	
	protected Renderer renderer;   
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}
	
	protected DrawComponent drawComponent;  
	public void setDrawComponent(DrawComponent drawComponent) {
		this.drawComponent = drawComponent;
	}
	
	/**
	 * @see org.nightlabs.editor2d.figures.RendererFigure#paint(java.awt.Graphics2D)
	 */
	public void paint(Graphics2D graphics) {
		if (renderer != null && drawComponent != null)  		
			renderer.paint(drawComponent, graphics);
	}  
	
	//  public boolean intersects(Rectangle rect) 
	//  {
	//    return super.intersects(rect);
	//  }
	//  
	//  protected Rectangle childrenBounds = new Rectangle(0,0,0,0);
	//  protected Rectangle getChildenBounds() 
	//  {
	//    for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
	//      IFigure child = (IFigure) iter.next();
	//      Rectangle childBounds = child.getBounds();
	//      childrenBounds.getUnion(childBounds);
	//    }
	//    return childrenBounds;
	//  }
	//   
	//  public Rectangle getBounds() {
	//    return getChildenBounds();
	//  }
}
