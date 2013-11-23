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

package org.nightlabs.editor2d.render;

import java.awt.Graphics2D;
import java.util.Collection;

import org.nightlabs.editor2d.DrawComponent;

/**
 * This is the Base-Interface for all Renderers
 * 
 * For each RenderMode of a DrawComponent there should
 * exist a certain Renderer-Implementation, which should display
 * the content of the DrawComponent corresponding to the context (RenderMode)
 * 
 * Implementation of Renderers for certain classes must cast the DrawComponent
 * in order to get access to the specialized content
 * 
 * Each Renderer can contain several {@link RenderContext}s which are make it
 * possible to render the content on different RenderOutputs/GraphicEngines
 * e.g. the in Java2D/Swing the Canvas to draw on is a {@link Graphics2D}
 * but in SWT the Canvas is a org.eclipse.swt.graphics.GC and
 * in Draw2D the Canvas is a org.eclipse.draw2d.Graphics, therefore exist
 * the different RenderContexts
 * 
 * @see DrawComponent#getRenderMode()
 * @see RenderModeManager
 * @see RenderContext
 * 
 * @author Daniel.Mazurek [AT] NightLabs [DOT] com
 *
 */
@SuppressWarnings("unchecked")
public interface Renderer
{
  
  /**
   * adds a {@link RenderContext} to the renderer
   * 
   * @param rc the {@link RenderContext} to add
   */
  void addRenderContext(RenderContext rc);
  
  /**
   * returns a Collection containing all added RenderContexts
   * 
   * @return a Collection containing all added RenderContexts
   */
  Collection<RenderContext> getRenderContexts();
    
  /**
   * returns the corresponding {@link RenderContext} for the given renderContextType
   * 
   * @param renderContextType the RenderContextType to get the corresponding RenderContext for
   * @return the corresponding RenderContext for the given renderContextType
   */
  RenderContext getRenderContext(String renderContextType);
}
