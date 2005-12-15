/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 19.05.2005 </p>
 * <p> Author: Daniel Mazurek </p>
 **/
package org.nightlabs.editor2d.figures;

import java.awt.Graphics2D;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.J2DGraphics;
import org.eclipse.gef.EditPart;

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
