/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 08.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.j2dswt;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DUtilities;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.render.Renderer;

public class DrawComponentPaintable 
implements IPaintable
{
	public static final Logger LOGGER = Logger.getLogger(DrawComponentPaintable.class.getName());
	
	protected DrawComponent dc;
	public DrawComponentPaintable(DrawComponent dc) {
		super();
		this.dc = dc;
	}

	public void paint(Control control, Graphics2D g2d) {
		paintDrawComponent(dc, g2d);
		LOGGER.debug("paint called !");
	}

	public void redraw(Control control, GC gc) {
//		LOGGER.debug("redraw called !");
	}

	public Rectangle2D getBounds(Control control) 
	{
		return J2DUtilities.toRectangle2D(control.getBounds());	
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
}
