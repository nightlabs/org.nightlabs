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
import java.awt.geom.Area;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.J2DGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ZoomListener;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.IVisible;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.render.Draw2DRenderContext;
import org.nightlabs.editor2d.render.RenderContext;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.util.J2DUtil;
import org.nightlabs.editor2d.util.RenderUtil;


public class DrawComponentFigure 
//extends SmartUpdateFigure 
extends Figure
implements RendererFigure
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(DrawComponentFigure.class.getName());
	
  private J2DGraphics j2d;
  private Graphics2D g2d;
    
  public void paint(Graphics2D graphics) 
  {
  	paintJ2D(graphics, drawComponent, renderer);
  }  
  
  public static void paintJ2D(Graphics2D graphics, DrawComponent drawComponent, Renderer renderer) 
  {
  	if (renderer == null && drawComponent != null)
  		renderer = drawComponent.getRenderer();
  	RenderUtil.paintJ2DRenderer(renderer, drawComponent, graphics);  	
  }
      
//  public static void checkDraw2D(Graphics graphics, DrawComponent dc, Renderer r) 
//  {
//    if (graphics instanceof J2DGraphics) 
//    {
//    	J2DGraphics j2d = (J2DGraphics) graphics;
//    	j2d.clipRect(null);
//      Graphics2D g2d = j2d.createGraphics2D();
//      g2d.setClip(null);
//      paintJ2D(g2d, dc, r);      
//      g2d.dispose();
//    }  	
//  }
  
  public void paint(Graphics graphics) 
  {  	
    if (graphics instanceof J2DGraphics) 
    {
      j2d = (J2DGraphics) graphics;
      j2d.clipRect(null);
      g2d = j2d.createGraphics2D();
      g2d.setClip(null);      
      paint(g2d);      
      g2d.dispose();
    }
    else {
//    	paintDrawComponent(drawComponent, graphics);
//    	LOGGER.debug("paint Draw2D");
    }
  }
      
  private Renderer renderer;   
  public void setRenderer(Renderer renderer) {
    this.renderer = renderer;
  }
  
  private DrawComponent drawComponent;  
  public void setDrawComponent(DrawComponent drawComponent) {
    this.drawComponent = drawComponent;
  }   
  
  private boolean contains = true;
  public void setContains(boolean contains) {
  	this.contains = contains;
  }
  public boolean isContains() {
  	return contains;
  }
  
  private Area outlineArea = null;  
  public boolean containsPoint(int x, int y) 
  {
  	if (contains) {
      if (drawComponent != null) {
        if (drawComponent instanceof ShapeDrawComponent) {
          ShapeDrawComponent sdc = (ShapeDrawComponent) drawComponent;
          if (sdc.isFill()) {
            return sdc.getGeneralShape().contains(x,y);
          }
          else 
          {
            if (outlineArea == null) 
            {
              Rectangle outerBounds = getBounds().getCopy();
              Rectangle innerBounds = getBounds().getCopy();
              outerBounds.expand((int)hitTolerance, (int)hitTolerance);
              innerBounds.shrink((int)hitTolerance, (int)hitTolerance);
              GeneralShape outerGS = (GeneralShape) sdc.getGeneralShape().clone();
              GeneralShape innerGS = (GeneralShape) sdc.getGeneralShape().clone();
              J2DUtil.transformGeneralShape(outerGS, getBounds(), outerBounds);
              J2DUtil.transformGeneralShape(innerGS, getBounds(), innerBounds);
              outlineArea = new Area(outerGS);
              Area innerArea = new Area(innerGS); 
              outlineArea.exclusiveOr(innerArea);             
            }
            boolean contains = outlineArea.contains(x,y);
            outlineArea = null;
            return contains;
          }
        }      
      }
      return super.containsPoint(x, y);  		
  	} 
  	else {
  		return false;
  	}
  }
  
  public static final double DEFAULT_HIT_TOLERANCE = 3;
  private double hitTolerance = DEFAULT_HIT_TOLERANCE;    
  public double getHitTolerance() {
    return hitTolerance;
  }
  public void setHitTolerance(double hitTolerance) {
    this.hitTolerance = hitTolerance;
  }
    
  protected ZoomListener zoomListener = new ZoomListener() {
    public void zoomChanged(double zoom) {
      hitTolerance = DEFAULT_HIT_TOLERANCE / zoom;
    }    
  };
  public ZoomListener getZoomListener() {
    return zoomListener;
  }

	public Rectangle getBounds() 
	{
		if (drawComponent != null)
			return J2DUtil.toDraw2D(drawComponent.getBounds());
		
		return super.getBounds();
	}  
 
	public static void paintDraw2DRenderer(Renderer r, DrawComponent dc, Graphics g) 
	{
		if (r != null && dc != null && g != null) 
		{				
			RenderContext rc = r.getRenderContext();
			if (rc instanceof Draw2DRenderContext) {
				logger.debug("rc instanceof Draw2DRenderContext");
				((Draw2DRenderContext)rc).paint(dc, g);					
			}
			else {
				rc = r.getRenderContext(Draw2DRenderContext.RENDER_CONTEXT_TYPE);
				if (rc != null) {
					logger.debug("r.getRenderContext(Draw2DRenderContext.RENDER_CONTEXT_TYPE) != null!");					
					Draw2DRenderContext d2drc = (Draw2DRenderContext) rc;
					d2drc.paint(dc, g);										
				} else
					logger.debug("r.getRenderContext(Draw2DRenderContext.RENDER_CONTEXT_TYPE) == null!");					
			}
		}					
	}
	
	public static void paintDrawComponent(DrawComponent dc, Graphics g) 
	{
		if (dc instanceof DrawComponentContainer) 
		{
			if (dc instanceof IVisible) {
				IVisible visible = (IVisible) dc; 
				if (!visible.isVisible())
					return;
			} 
			DrawComponentContainer dcContainer = (DrawComponentContainer) dc;
			for (Iterator it = dcContainer.getDrawComponents().iterator(); it.hasNext(); ) {
				DrawComponent drawComponent = (DrawComponent) it.next();
				paintDrawComponent(drawComponent, g);
			}
		}
		else {
			Renderer r = dc.getRenderer();
			paintDraw2DRenderer(r, dc, g);			
		}		
	}
	
}
