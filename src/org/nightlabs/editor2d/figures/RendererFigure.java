/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 19.05.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.figures;

import java.awt.Graphics2D;

import org.eclipse.draw2d.IFigure;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.Renderer;


public interface RendererFigure 
extends IFigure
{
  public void setRenderer(Renderer renderer);
  public void setDrawComponent(DrawComponent drawComponent);
  public void paint(Graphics2D graphics);
}
