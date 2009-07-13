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

import java.awt.Color;
import java.awt.Graphics2D;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.IVisible;
import org.nightlabs.editor2d.util.RenderUtil;
import org.nightlabs.util.ColorUtil;

/**
 * The Standard Implementation of the J2DRenderContext-Interface
 * does by default nothing.
 * It checks if the given DrawComponent is a DrawComponentContainer
 * and if so, it paints all its children on a Graphics2D
 * but only if it is not an {@link IVisible} which visible state is set to false
 * 
 * @author Daniel.Mazurek [AT] NightLabs [DOT] com
 */
public class J2DBaseRenderer
implements J2DRenderContext
{
  public J2DBaseRenderer() {
    super();
    init();
  }
  
  /**
	 * The Standard Implementation of the paint-Method does by default nothing.
	 * It checks if the given DrawComponent is a DrawComponentContainer
	 * and if so, it paints all its children,
	 * but only if it is not an {@link IVisible} which visible state is set to false
   */
  public void paint(DrawComponent dc, Graphics2D g2d)
  {
  	RenderUtil.paintDrawComponent(dc, g2d);
  }
    
  /**
   * Inheritans of this class can override this Method
   * in order to initialize things, needed for rendering.
   *
   * By Default this Method is empty.
   * This Method will be called in the Constructor
   */
  protected void init()
  {
  };
  
  protected String renderContextType = J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D;
  public String getRenderContextType() {
  	return renderContextType;
  }
  public void setRenderContextType(String newRenderContextType) {
  	this.renderContextType = newRenderContextType;
  }
  
  protected Color makeBrighter(Color c) {
  	return ColorUtil.brighter(c, 2);
  }
  
  protected Color makeDarker(Color c) {
  	return ColorUtil.darker(c, 2);
  }
}
