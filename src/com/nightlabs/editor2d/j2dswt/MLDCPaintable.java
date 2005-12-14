/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 15.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.j2dswt;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DUtilities;

import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.DrawComponentContainer;
import com.nightlabs.editor2d.MultiLayerDrawComponent;
import com.nightlabs.editor2d.render.Renderer;

public class MLDCPaintable implements IPaintable
{
	protected MultiLayerDrawComponent mldc;
	public MLDCPaintable(MultiLayerDrawComponent mldc) 
	{
		this.mldc = mldc;
	}
	
//	public void paint(Control control, Graphics2D g2d)
//	{
//		for (Iterator itLayers = mldc.getDrawComponents().iterator(); itLayers.hasNext(); ) {
//			Layer l = (Layer) itLayers.next();
//			for (Iterator it = l.getDrawComponents().iterator(); it.hasNext(); ) {
//				DrawComponent dc = (DrawComponent) it.next();
//				Renderer r = dc.getRenderer();
//				if (r != null) 
//					r.paint(dc, g2d);
//			}
//		}
//	}
	public void paint(Control control, Graphics2D g2d)
	{
		for (Iterator itLayers = mldc.getDrawComponents().iterator(); itLayers.hasNext(); ) {
			DrawComponent dc = (DrawComponent) itLayers.next();
			paintDrawComponent(dc, g2d);
		}
	}

	protected void paintDrawComponent(DrawComponent dc, Graphics2D g2d) 
	{
		if (dc instanceof DrawComponentContainer) {
			DrawComponentContainer dcContainer = (DrawComponentContainer) dc;
			for (Iterator it = dcContainer.getDrawComponents().iterator(); it.hasNext(); ) {
				DrawComponent drawComponent = (DrawComponent) it.next();
				paintDrawComponent(drawComponent, g2d);
			}
		}
		else {
			Renderer r = dc.getRenderer();
			if (r != null) 
				r.paint(dc, g2d);			
		}
	}
	
	public void redraw(Control control, GC gc)
	{
		
	}

	public Rectangle2D getBounds(Control control)
	{
		return J2DUtilities.toRectangle2D(control.getBounds());
	}
}
